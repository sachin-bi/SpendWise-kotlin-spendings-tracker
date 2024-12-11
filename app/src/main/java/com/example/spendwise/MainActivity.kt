package com.example.spendwise

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spendwise.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    // private val SMS_PERMISSION_CODE = 101
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        replaceFragment(Home())

        binding.bottomNavigationView2.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home_item -> replaceFragment(Home())
                R.id.transitions_item -> replaceFragment(Transactions())
                R.id.message_item -> replaceFragment(Messages())
                R.id.profile_item -> replaceFragment(Profile())

                else -> {
                    Toast.makeText(applicationContext, "Unknown item selected", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }






//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }


//        if (checkSmsPermission()) {
//            displaySms()
//        } else {
//            requestSmsPermission()
//        }
//
//        val switchToBankMsgs= findViewById<Switch>(R.id.switchToBankMsgs)
//
//        switchToBankMsgs.setOnClickListener{
//            if (switchToBankMsgs.isChecked) {
//                Toast.makeText(applicationContext, "Showing bank Messages", Toast.LENGTH_SHORT).show()
//                displaySms(showBankMessages = true) // Filter for bank messages
//            }
//            else{
//                Toast.makeText(applicationContext, "Showing All Messages", Toast.LENGTH_SHORT).show()
//                displaySms(showBankMessages = false) // Show all messages
//
//            }
//        }


    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

//
//private fun checkSmsPermission(): Boolean {
//
//    return ContextCompat.checkSelfPermission(
//        this,
//        Manifest.permission.READ_SMS
//    ) == PackageManager.PERMISSION_GRANTED
//}
//
//private fun requestSmsPermission() {
//    ActivityCompat.requestPermissions(
//        this,
//        arrayOf(Manifest.permission.READ_SMS),
//        SMS_PERMISSION_CODE
//    )
//}
//
//override fun onRequestPermissionsResult(
//    requestCode: Int,
//    permissions: Array<out String>,
//    grantResults: IntArray
//) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    if (requestCode == SMS_PERMISSION_CODE) {
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // Permission was granted
//            Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show()
//            displaySms()    // Proceed with functionality that requires SMS permission
//        } else {
//            Toast.makeText(this, "Permission Denied - line 69", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//    private fun displaySms(showBankMessages: Boolean = false) {
//        val smsList = mutableListOf<Triple<String, String, String>>() // Store sender, message, and date
//        val listView = findViewById<ListView>(R.id.lvSms)
//
//        // getting the sms from mobile's db
//        val cursor : Cursor?  = contentResolver.query(
//            Telephony.Sms.CONTENT_URI,
//            null,
//            null,
//            null,
//            Telephony.Sms.DATE + " DESC"
//        )
//
//        // cursor?.let { ... }
//        //: This checks if the cursor is not null (meaning the query was successful).
//        // If it is, the code inside the curly braces executes.
//        cursor?.let {
//            val addressColumn = it.getColumnIndex(Telephony.Sms.ADDRESS)
//            val bodyColumn = it.getColumnIndex(Telephony.Sms.BODY)
//            val dateColumn = it.getColumnIndex(Telephony.Sms.DATE)
//
//            while (it.moveToNext()) {
//                val sender = it.getString(addressColumn)
//                val message = it.getString(bodyColumn)
//                val dateMillis = it.getLong(dateColumn)
//                val date = android.text.format.DateFormat.format("dd-MM-yyyy hh:mm a", dateMillis) // Format date
//
//                //smsList.add(Triple(sender, message, date.toString())) // Store sender, message, and date
//
//                // Filter logic for bank messages
//                if (!showBankMessages || isBankMessage(sender,message)) {
//                    smsList.add(Triple(sender, message, date.toString())) // Store sender, message, and date
//                }
//            }
//            it.close()
//        }
//
//        // Use the custom adapter to display sender, message, and date
//        val adapter = SmsAdapter(this, R.layout.list_item_sms, smsList)
//        listView.adapter = adapter
//    }
//
//    // Utility function to determine if a sender is a bank
//    private fun isBankMessage(sender: String, message: String): Boolean {
//        // Common patterns for bank sender IDs
//        val bankPatterns = listOf(
//            Regex("^[A-Za-z]{2,}-\\d{2,}$"),  // Example: "AX-12345"
//            Regex("^[A-Za-z]{3,}$"),         // Example: "ICICI", "SBI"
//            Regex("^[A-Za-z]{2,}\\d{1,}$"),  // Example: "ICICI1", "HDFC123"
//            Regex("^[A-Za-z]+\\d+$")         // Example: "Bank123", "MyBank456"
//        )
//
//        // Custom sender names (specific to region or known banks)
//        val knownBanks = listOf(
//            "ICICIBANK", "SBIBANK", "HDFCBANK", "AXISBANK", "PNB",
//            "CITIBANK", "BOB", "KOTAKBANK", "YESBANK", "IDFCFIRST"
//        )
//
//        // Keywords commonly found in bank messages
//        val bankKeywords = listOf(
//            "transaction", "debit", "credit", "account", "balance",
//            "payment", "statement", "otp", "loan", "bank"
//        )
//
//        // Check if sender contains only numbers (not a bank message)
//        if (sender.matches(Regex("^\\d+$"))) {
//            return false
//        }
//
//        // Check if sender matches any pattern
//        val matchesPattern = bankPatterns.any { pattern -> sender.matches(pattern) }
//
//        // Check if sender is in the known banks list (ignoring case)
//        val isKnownBank = knownBanks.any { bank -> sender.equals(bank, ignoreCase = true) }
//
//        // Check if message contains any bank-related keywords
//        val containsBankKeywords = bankKeywords.any { keyword ->
//            message.contains(keyword, ignoreCase = true)
//        }
//
//        // Return true if either sender or message matches bank criteria
//        return matchesPattern || isKnownBank || containsBankKeywords
//    }
//
//
//


}

