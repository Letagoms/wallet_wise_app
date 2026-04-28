package com.example.wallet_wise_app

/*every expense has an amount, date, time, category, description, and optional receipt photo
id = 0 means "I don't have an ID yet" meaning SQLite assigns one automatically when saved
receiptPath = null means the photo is optional***/
data class Expense(
    val id: Int = 0,
    val amount: Double,
    val date: String,
    val time: String,
    val category: String,
    val description: String,
    val receiptPath: String? = null
)