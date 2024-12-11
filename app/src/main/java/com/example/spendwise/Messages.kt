package com.example.spendwise

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.Telephony
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Messages.newInstance] factory method to
 * create an instance of this fragment.
 */
class Messages : Fragment() {

    private val SMS_PERMISSION_CODE = 101

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messages, container, false)
        val switchToBankMsgs = view.findViewById<Switch>(R.id.switchToBankMsgs)
        val listView = view.findViewById<ListView>(R.id.lvSms)

        // Check SMS permission
        if (checkSmsPermission()) {
            displaySms(listView, false)
        } else {
            requestSmsPermission()
        }

        // Toggle bank messages filter
        switchToBankMsgs.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(context, "Showing bank messages", Toast.LENGTH_SHORT).show()
                displaySms(listView, true)
            } else {
                Toast.makeText(context, "Showing all messages", Toast.LENGTH_SHORT).show()
                displaySms(listView, false)
            }
        }

        return view

    }
    private fun checkSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_SMS),
            SMS_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "SMS Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displaySms(listView: ListView, showBankMessages: Boolean) {
        val smsList = mutableListOf<Triple<String, String, String>>() // Sender, message, date

        val cursor: Cursor? = requireContext().contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            null,
            null,
            Telephony.Sms.DATE + " DESC"
        )

        cursor?.let {
            val addressColumn = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyColumn = it.getColumnIndex(Telephony.Sms.BODY)
            val dateColumn = it.getColumnIndex(Telephony.Sms.DATE)

            while (it.moveToNext()) {
                val sender = it.getString(addressColumn)
                val message = it.getString(bodyColumn)
                val dateMillis = it.getLong(dateColumn)
                val date = DateFormat.format("dd-MM-yyyy hh:mm a", dateMillis).toString()

                // Filter logic for bank messages
                if (!showBankMessages || isBankMessage(sender, message)) {
                    smsList.add(Triple(sender, message, date))
                }
            }
            it.close()
        }

        val adapter = SmsAdapter(requireContext(), R.layout.list_item_sms, smsList)
        listView.adapter = adapter
    }

        private fun isBankMessage(sender: String, message: String): Boolean {
        // Common patterns for bank sender IDs
        val bankPatterns = listOf(
            Regex("^[A-Za-z]{2,}-\\d{2,}$"),  // Example: "AX-12345"
            Regex("^[A-Za-z]{3,}$"),         // Example: "ICICI", "SBI"
            Regex("^[A-Za-z]{2,}\\d{1,}$"),  // Example: "ICICI1", "HDFC123"
            Regex("^[A-Za-z]+\\d+$")         // Example: "Bank123", "MyBank456"
        )

        // Custom sender names (specific to region or known banks)
        val knownBanks = listOf(
            "ICICIBANK", "SBIBANK", "HDFCBANK", "AXISBANK", "PNB",
            "CITIBANK", "BOB", "KOTAKBANK", "YESBANK", "IDFCFIRST"
        )

        // Keywords commonly found in bank messages
        val bankKeywords = listOf(
            "transaction", "debit", "credit", "account", "balance",
            "payment", "statement", "otp", "loan", "bank"
        )

        // Check if sender contains only numbers (not a bank message)
        if (sender.matches(Regex("^\\d+$"))) {
            return false
        }

        // Check if sender matches any pattern
        val matchesPattern = bankPatterns.any { pattern -> sender.matches(pattern) }

        // Check if sender is in the known banks list (ignoring case)
        val isKnownBank = knownBanks.any { bank -> sender.equals(bank, ignoreCase = true) }

        // Check if message contains any bank-related keywords
        val containsBankKeywords = bankKeywords.any { keyword ->
            message.contains(keyword, ignoreCase = true)
        }

        // Return true if either sender or message matches bank criteria
        return matchesPattern || isKnownBank || containsBankKeywords
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Messages.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Messages().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}