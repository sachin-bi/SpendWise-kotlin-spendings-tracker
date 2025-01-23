package com.example.spendwise

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.spendwise.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

     private val SMS_PERMISSION_CODE = 101
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        // Check SMS permission when the activity starts
        if (checkSmsPermission()) {
            // Permission is granted
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            // Proceed with functionality that requires SMS permission (if needed)
        } else {
            // Permission is not granted, request it
            requestSmsPermission()
        }
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





    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }


    // Check if SMS permission is granted
    private fun checkSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request SMS permission
    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_SMS),
            SMS_PERMISSION_CODE
        )
    }



}

