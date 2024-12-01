package com.example.msgs

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.database.Cursor
import android.provider.Telephony
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private val SMS_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (checkSmsPermission()) {
            displaySms()
        } else {
            requestSmsPermission()
        }
    }


private fun checkSmsPermission(): Boolean {

    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_SMS
    ) == PackageManager.PERMISSION_GRANTED
}

private fun requestSmsPermission() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.READ_SMS),
        SMS_PERMISSION_CODE
    )
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == SMS_PERMISSION_CODE) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission was granted
            Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show()
            displaySms()    // Proceed with functionality that requires SMS permission
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}
    private fun displaySms() {
        val smsList = mutableListOf<Pair<String, String>>()
        val listView = findViewById<ListView>(R.id.lvSms)

        val cursor : Cursor?  = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            null,
            null,
            Telephony.Sms.DATE + " DESC"
        )

        cursor?.let {
            val addressColumn = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyColumn = it.getColumnIndex(Telephony.Sms.BODY)

            while (it.moveToNext()) {
                val sender = it.getString(addressColumn)
                val message = it.getString(bodyColumn)
                smsList.add(Pair(sender, message)) // Store as Pair(sender, message)
            }
            it.close()
        }

        // Use the custom adapter
        val adapter = SmsAdapter(this, R.layout.list_item_sms, smsList)
        listView.adapter = adapter
    }

//    private fun displaySms() {
//        val smsList = mutableListOf<String>()
//        val listView = findViewById<ListView>(R.id.lvSms)
//
//        val cursor: Cursor? = contentResolver.query(
//            Telephony.Sms.CONTENT_URI,
//            null,
//            null,
//            null,
//            Telephony.Sms.DATE + " DESC"
//        )
//
//        cursor?.let {
//            val addressColumn = it.getColumnIndex(Telephony.Sms.ADDRESS)
//            val bodyColumn = it.getColumnIndex(Telephony.Sms.BODY)
//
//            while (it.moveToNext()) {
//                val address = it.getString(addressColumn)
//                val body = it.getString(bodyColumn)
//                smsList.add("From: $address\nMessage: $body")
//            }
//            it.close()
//        }
//
//        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, smsList)
//        listView.adapter = adapter
//    }

}