package com.example.wallet_wise_app

data class Category(
    val id: Int = 0,
    val name: String,
    val minGoal: Double = 0.0,
    val maxGoal: Double = 0.0,
    val colorResId: Int = android.R.color.holo_blue_dark,
    val iconResId: Int = 0,
    val userId: Int = 0
)