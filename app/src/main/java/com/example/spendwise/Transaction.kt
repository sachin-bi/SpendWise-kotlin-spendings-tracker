package com.example.spendwise.model

data class Transaction(
    val amount: String,
    val date: String,
    val time: String,
    val isCredit: Boolean // true if credited, false if debited
)
