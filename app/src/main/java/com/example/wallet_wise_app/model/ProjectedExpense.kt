package com.example.wallet_wise_app.model

data class ProjectedExpense(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val date: String,  // Format: yyyy-MM-dd
    val userId: Int
)