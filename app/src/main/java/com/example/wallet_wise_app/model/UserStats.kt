// model/UserStats.kt
package com.example.wallet_wise_app.model

data class UserStats(
    val id: Int = 0,
    val userId: Int,
    val loginCount: Int = 0,
    val expenseCount: Int = 0,
    val goalCount: Int = 0,
    val lastLoginDate: String? = null
)