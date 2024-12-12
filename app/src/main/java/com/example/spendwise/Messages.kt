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
import com.example.spendwise.adapter.TransactionAdapter
import com.example.spendwise.model.TransactionData


class Messages : Fragment() {

    private val SMS_PERMISSION_CODE = 101


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

        // Query for only received messages
        val cursor: Cursor? = requireContext().contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            Telephony.Sms.TYPE + " = ?",
            arrayOf(Telephony.Sms.MESSAGE_TYPE_INBOX.toString()),
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
private fun displaySms2(listView: ListView, showBankMessages: Boolean) {
    val transactions = mutableListOf<TransactionData>()

    val cursor: Cursor? = requireContext().contentResolver.query(
        Telephony.Sms.CONTENT_URI,
        null,
        Telephony.Sms.TYPE + " = ?",
        arrayOf(Telephony.Sms.MESSAGE_TYPE_INBOX.toString()),
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
            val date = DateFormat.format("dd-MM-yyyy", dateMillis).toString()
            val time = DateFormat.format("hh:mm a", dateMillis).toString()

            if (!showBankMessages || isBankMessage(sender, message)) {
                val transaction = parseTransaction(sender, message, date, time)
                if (transaction != null) {
                    transactions.add(transaction)
                }
            }
        }
        it.close()
    }

//    val adapter = TransactionAdapter(requireContext(), R.layout.list_item_sms, transactions)
//    val adapter = TransactionAdapter(requireContext(), transactions)
    val adapter = TransactionAdapter(requireContext(), transactions)
    listView.adapter = adapter
}

//    private
    fun parseTransaction(sender: String, message: String, date: String, time: String): TransactionData? {
        // Attempt to extract amount and determine if it's a credit or debit
        val amountRegex = Regex("\\b(?:Rs\\.?|INR)?\\s?(\\d+(?:\\.\\d{1,2})?)\\b", RegexOption.IGNORE_CASE)
        val creditKeywords = listOf("credited", "credit", "deposit")
        val debitKeywords = listOf("debited", "debit", "withdrawal")

        val amountMatch = amountRegex.find(message)
        val isCredit = creditKeywords.any { message.contains(it, ignoreCase = true) }
        val isDebit = debitKeywords.any { message.contains(it, ignoreCase = true) }

        return if (amountMatch != null && (isCredit || isDebit)) {
            val amount = amountMatch.groupValues[1]
            TransactionData(
                sender = sender,
                messageBody = message,
                amount = amount,
                date = date,
                time = time,
                isCredit = isCredit
            )
        } else {
            null
        }
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
            "payment", "bank", "debited", "credited",
        )

        // Check if sender contains only numbers (not a bank message)
//        if (sender.matches(Regex("^\\d+$"))) {
//            return false
//        }

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


}