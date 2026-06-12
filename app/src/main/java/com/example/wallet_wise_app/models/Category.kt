package com.example.wallet_wise_app.models

// Plain Kotlin data class for API payloads
data class Category(
    val id: Int = 0,
    val name: String,
    val minGoal: Double,
    val maxGoal: Double,
    val colorResId: Int,   // default resource ID
    val iconResId: Int     // default resource ID
)