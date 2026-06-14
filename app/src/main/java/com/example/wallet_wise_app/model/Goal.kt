// models/Goal.kt
package com.example.wallet_wise_app.models

data class Goal(
    val goalId: Int = 0,           // Primary key (0 means new goal, not saved yet)
    val minimumGoal: Int,          // Minimum savings/spending goal
    val maximumGoal: Int,          // Maximum savings/spending goal
    val userId: Int                // Which user this goal belongs to (foreign key)
)