// repositories/GoalRepository.kt
package com.example.wallet_wise_app.repository

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.model.Goal

class GoalRepository(private val context: Context) {

    // FIXED: Use getInstance() instead of constructor
    private val dbHelper = DatabaseHelper.getInstance(context)

    fun save(goal: Goal): Goal {
        val id = dbHelper.insertGoal(goal)
        return goal.copy(goalId = id.toInt())
    }

    fun getAllGoals(): List<Goal> {
        return dbHelper.getAllGoals()
    }

    fun getGoalsByUserId(userId: Int): List<Goal> {
        return dbHelper.getGoalsByUserId(userId)
    }
}