package com.example.spendwise

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spendwise.model.TransactionData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize BarChart
        val barChart: BarChart = view.findViewById(R.id.barChart)
        // val summaryRecyclerView: RecyclerView = view.findViewById(R.id.summaryRecyclerView)

        // Fetch last 7 days' transactions
        val transactions = TransactionUtils.getTransactions(requireContext(), true)
        val weeklySummary = getWeeklySummary(transactions)

        // Set up the BarChart
        setupBarChart(barChart, weeklySummary)

        // Set up the RecyclerView
        // summaryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // summaryRecyclerView.adapter = SummaryAdapter(weeklySummary)

        // Initialize RecyclerView
        val summaryRecyclerView: RecyclerView = view.findViewById(R.id.summaryRecyclerView)
        setupRecyclerView(summaryRecyclerView, weeklySummary)
        return view
    }
    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        weeklySummary: List<DailyTransactionSummary>
    ) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true  // Reverse the order of items
            stackFromEnd = true   // Start stacking items from the end
        }
        recyclerView.adapter = SummaryAdapter(weeklySummary)
    }

    private fun getWeeklySummary(transactions: List<TransactionData>): List<DailyTransactionSummary> {
        val calendar = Calendar.getInstance()
        val summaries = mutableMapOf<String, DailyTransactionSummary>()

        for (i in 0 until 7) {
            val date = DateFormat.format("dd-MM-yyyy", calendar.time).toString()
            summaries[date] = DailyTransactionSummary(date, 0f, 0f)
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        transactions.forEach { transaction ->
            val date = transaction.date
            summaries[date]?.let { summary ->
                if (transaction.isCredit) {
                    summaries[date] = summary.copy(totalCredit = summary.totalCredit + transaction.amount.toFloat())
                } else {
                    summaries[date] = summary.copy(totalDebit = summary.totalDebit + transaction.amount.toFloat())
                }
            }
        }
        // check whats the return..
        return summaries.values.toList().sortedBy { it.date }
    }

    // need to explain
    private fun setupBarChart(barChart: BarChart, weeklySummary: List<DailyTransactionSummary>) {
        val creditEntries = ArrayList<BarEntry>()
        val debitEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        weeklySummary.forEachIndexed { index, summary ->
            creditEntries.add(BarEntry(index.toFloat(), summary.totalCredit))
            debitEntries.add(BarEntry(index.toFloat(), summary.totalDebit))
            labels.add(summary.date)
        }

        val creditDataSet = BarDataSet(creditEntries, "Credits").apply {
            color = resources.getColor(R.color.creditColor, null)
        }
        val debitDataSet = BarDataSet(debitEntries, "Debits").apply {
            color = resources.getColor(R.color.debitColor, null)
        }

        val barData = BarData(creditDataSet, debitDataSet).apply {
            barWidth = 0.3f // Width of each bar
        }

        barChart.apply {
            data = barData
            groupBars(0f, 0.4f, 0.05f) // Group the bars
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                isGranularityEnabled = true
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            animateY(1000)
            invalidate() // Refresh chart
        }
    }

    data class DailyTransactionSummary(
        val date: String,
        val totalCredit: Float,
        val totalDebit: Float
    )
}
