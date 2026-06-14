// services/GoalService.kt
package com.example.wallet_wise_app.services

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.model.Goal

class GoalService(private val context: Context) {

    private val dbHelper = DatabaseHelper.getInstance(context)

    fun addGoal(
        minimumGoal: Int,
        maximumGoal: Int,
        userId: Int
    ): Goal {
        if (minimumGoal < 0) {
            throw Exception("Minimum goal cannot be negative")
        }

        if (maximumGoal < 0) {
            throw Exception("Maximum goal cannot be negative")
        }

        if (minimumGoal > maximumGoal) {
            throw Exception("Minimum goal cannot be greater than maximum goal")
        }

        if (userId <= 0) {
            throw Exception("Valid user ID is required")
        }

        val goal = Goal(
            goalId = 0,
            minimumGoal = minimumGoal,
            maximumGoal = maximumGoal,
            userId = userId
        )

        val id = dbHelper.insertGoal(goal)
        return goal.copy(goalId = id.toInt())
    }

    fun getAllGoals(): List<Goal> {
        return dbHelper.getAllGoals()
    }

    fun getGoalsByUserId(userId: Int): List<Goal> {
        return dbHelper.getGoalsByUserId(userId)
    }

    fun checkGoalStatus(userId: Int, currentSpending: Double): String {
        val userGoals = getGoalsByUserId(userId)

        if (userGoals.isEmpty()) {
            return "No goals set for this user"
        }

        val goal = userGoals.first()

        return when {
            currentSpending < goal.minimumGoal -> "❌ Below minimum goal. Spend more to reach R${goal.minimumGoal}"
            currentSpending > goal.maximumGoal -> "⚠️ Exceeded maximum goal! Stay within R${goal.maximumGoal}"
            else -> "✅ On track! Between R${goal.minimumGoal} and R${goal.maximumGoal}"
        }
    }
}