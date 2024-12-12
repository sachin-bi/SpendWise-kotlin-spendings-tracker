package com.example.spendwise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SummaryAdapter(private val summaries: List<Home.DailyTransactionSummary>) :
    RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    class SummaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val creditTextView: TextView = view.findViewById(R.id.creditTextView)
        val debitTextView: TextView = view.findViewById(R.id.debitTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_summary, parent, false)
        return SummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val summary = summaries[position]
        holder.dateTextView.text = summary.date
        holder.creditTextView.text = "Credits: ₹${summary.totalCredit}"
        holder.debitTextView.text = "Debits: ₹${summary.totalDebit}"
    }

    override fun getItemCount(): Int = summaries.size
}
