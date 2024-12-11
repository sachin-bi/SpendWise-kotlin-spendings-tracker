package com.example.spendwise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.spendwise.adapter.TransactionAdapter
import com.example.spendwise.model.Transaction

class Transactions : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)
        val listView: ListView = view.findViewById(R.id.transaction_list)

        // Sample transactions
        val transactions = listOf(
            Transaction("₹5000.00", "22/11/2024", "11:00 PM", true),
            Transaction("₹490.00", "22/11/2024", "10:40 AM", false),
            Transaction("₹20.00", "22/11/2024", "10:00 AM", false),
            Transaction("₹288.00", "21/11/2024", "09:21 PM", true),
            Transaction("₹455.00", "21/11/2024", "10:56 AM", false)
        )

        // Set adapter
        val adapter = TransactionAdapter(requireContext(), transactions)
        listView.adapter = adapter

        return view
    }
}
