package com.example.spendwise

import android.content.Context
import android.database.Cursor
import android.provider.Telephony
import android.text.format.DateFormat
import com.example.spendwise.model.TransactionData

object TransactionUtils {

    fun getTransactions(context: Context, showBankMessages: Boolean): List<TransactionData> {
        val transactions = mutableListOf<TransactionData>()

        val cursor: Cursor? = context.contentResolver.query(
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

        return transactions
    }

    private fun isBankMessage(sender: String, message: String): Boolean {
        // Same logic as provided in Transactions fragment
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
        // Same logic as provided in Transactions fragment
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
