package com.example.spendwise

import android.database.Cursor
import android.os.Bundle
import android.provider.Telephony
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.spendwise.adapter.TransactionAdapter
import com.example.spendwise.model.TransactionData

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
//        val transactionsData = listOf(
//            TransactionData("₹5000.00", "22/11/2024", "11:00 PM", true),
//            TransactionData("₹490.00", "22/11/2024", "10:40 AM", false),
//            TransactionData("₹20.00", "22/11/2024", "10:00 AM", false),
//            TransactionData("₹288.00", "21/11/2024", "09:21 PM", true),
//            TransactionData("₹455.00", "21/11/2024", "10:56 AM", false)
//        )

        // Set adapter
//        val adapter = TransactionAdapter(requireContext(), transactionsData)
//        listView.adapter = adapter
        displaySms2(listView,true)

        return view
    }


    private fun displaySms2(listView: ListView, showBankMessages: Boolean) {

        // data - how to access this transactions from home.kt

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


    private fun parseTransaction(sender: String, message: String, date: String, time: String): TransactionData? {
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

}
