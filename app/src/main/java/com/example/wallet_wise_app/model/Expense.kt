// models/Expense.kt
package com.example.wallet_wise_app.models

data class Expense(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val date: String,
    val startTime: String,
    val endTime: String,
    val photo: String,
    val description: String
)