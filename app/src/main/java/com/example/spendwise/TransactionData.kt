package com.example.spendwise.model

data class TransactionData(
    val sender: String,
    val messageBody: String,
    val amount: String,
    val date: String,
    val time: String,
    val isCredit: Boolean // true if credited, false if debited
)
