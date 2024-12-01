package com.example.msgs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SmsAdapter(
    context: Context,
    private val resource: Int,
    private val smsList: List<Pair<String, String>> // List of Pair(sender, message)
) : ArrayAdapter<Pair<String, String>>(context, resource, smsList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // Find views in the custom layout
        val senderTextView = view.findViewById<TextView>(R.id.tvSender)
        val messageTextView = view.findViewById<TextView>(R.id.tvMessage)

        // Get current SMS data
        val sms = smsList[position]
        senderTextView.text = "From: ${sms.first}"   // Sender
        messageTextView.text = sms.second           // Message body

        return view
    }
}

