// services/GoalService.kt
package com.example.wallet_wise_app.services

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.database.GoalTable
import com.example.wallet_wise_app.models.Goal

class GoalService(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // ========== ADD NEW GOAL ==========
    // Validates business rules before saving
    fun addGoal(
        minimumGoal: Int,
        maximumGoal: Int,
        userId: Int
    ): Goal {
        // BUSINESS RULE 1: Minimum goal cannot be negative
        if (minimumGoal < 0) {
            throw Exception("Minimum goal cannot be negative")
        }

        // BUSINESS RULE 2: Maximum goal cannot be negative
        if (maximumGoal < 0) {
            throw Exception("Maximum goal cannot be negative")
        }

        // BUSINESS RULE 3: Minimum goal cannot be greater than maximum goal
        if (minimumGoal > maximumGoal) {
            throw Exception("Minimum goal cannot be greater than maximum goal")
        }

        // BUSINESS RULE 4: User ID must be valid (positive number)
        if (userId <= 0) {
            throw Exception("Valid user ID is required")
        }

        // Create Goal object
        val goal = Goal(
            goalId = 0,
            minimumGoal = minimumGoal,
            maximumGoal = maximumGoal,
            userId = userId
        )

        // Save to database
        val db = dbHelper.writableDatabase
        val id = GoalTable.insert(db, goal)
        db.close()

        return goal.copy(goalId = id.toInt())
    }

    // ========== GET ALL GOALS ==========
    fun getAllGoals(): List<Goal> {
        val db = dbHelper.readableDatabase
        val goals = GoalTable.getAll(db)
        db.close()
        return goals
    }

    // ========== GET GOALS BY USER ==========
    fun getGoalsByUserId(userId: Int): List<Goal> {
        val db = dbHelper.readableDatabase
        val goals = GoalTable.getByUserId(db, userId)
        db.close()
        return goals
    }

    // ========== CHECK IF USER HAS REACHED GOAL ==========
    // Compares user's total spending against their goals
    fun checkGoalStatus(userId: Int, currentSpending: Int): String {
        val userGoals = getGoalsByUserId(userId)

        if (userGoals.isEmpty()) {
            return "No goals set for this user"
        }

        val goal = userGoals.first() // Use first goal for simplicity

        return when {
            currentSpending < goal.minimumGoal -> "❌ Below minimum goal. Spend more to reach R${goal.minimumGoal}"
            currentSpending > goal.maximumGoal -> "⚠️ Exceeded maximum goal! Stay within R${goal.maximumGoal}"
            else -> "✅ On track! Between R${goal.minimumGoal} and R${goal.maximumGoal}"
        }
    }
}