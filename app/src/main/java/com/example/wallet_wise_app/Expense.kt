package com.example.wallet_wise_app

data class Expense(
    val id: Int = 0,
    val amount: Double,
    val date: String,
    val time: String,
    val category: String,
    val description: String,
    val receiptPath: String? = null
)