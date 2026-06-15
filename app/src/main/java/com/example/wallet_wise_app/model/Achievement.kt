// model/Achievement.kt
package com.example.wallet_wise_app.model

data class Achievement(
    val id: Int = 0,
    val name: String,
    val description: String,
    val requirement: String,  // "expense_count", "login_count", "goal_count"
    val requiredValue: Int,
    val icon: String,  // "🔥", etc.
    val isUnlocked: Boolean = false,
    val unlockedAt: String? = null,
    val userId: Int
)