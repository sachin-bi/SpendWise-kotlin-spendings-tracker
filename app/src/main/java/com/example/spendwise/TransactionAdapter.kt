package com.example.spendwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.widget.BaseAdapter
import com.example.spendwise.R
import com.example.spendwise.model.TransactionData

class TransactionAdapter(private val context: Context, private val transactions: List<TransactionData>) : BaseAdapter() {

    override fun getCount(): Int = transactions.size

    override fun getItem(position: Int): Any = transactions[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false)

        val amountView: TextView = view.findViewById(R.id.amount)
        val dateView: TextView = view.findViewById(R.id.date)
        val timeView: TextView = view.findViewById(R.id.time)

        val transaction = transactions[position]

        amountView.text = transaction.amount
        dateView.text = transaction.date
        timeView.text = transaction.time

        // Set background color based on transaction type
        val backgroundColor = if (transaction.isCredit) {
            ContextCompat.getColor(context, R.color.credit_green)
        } else {
            ContextCompat.getColor(context, R.color.debit_red)
        }
        view.setBackgroundColor(backgroundColor)

        return view
    }
}
