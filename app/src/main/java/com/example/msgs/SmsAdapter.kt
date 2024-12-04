package com.example.msgs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes

class SmsAdapter(
    context: Context,
    // private val resource: Int,
    @LayoutRes private val layoutResource: Int,
    private val smsList: List<Triple<String,String, String>>  // List of Pair(sender, message)
) : ArrayAdapter<Triple<String, String, String>>(context, layoutResource, smsList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutResource, parent, false)



        // Find views in the custom layout
        val senderTextView = view.findViewById<TextView>(R.id.senderTextView)
        val messageTextView = view.findViewById<TextView>(R.id.messageTextView)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)

        // Get current SMS data
//        val sms = smsList[position]
        //senderTextView.text = "From: ${sms.first}"   // Sender
        //        messageTextView.text = sms.second           // Message body
        val (sender, message, date) = smsList[position]
        senderTextView.text = "From: $sender"
        messageTextView.text = "Message: $message"
        dateTextView.text = "Date: $date"


        return view
    }
}

