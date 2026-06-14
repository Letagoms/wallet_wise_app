// repositories/GoalRepository.kt
package com.example.wallet_wise_app.repositories

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.database.GoalTable
import com.example.wallet_wise_app.models.Goal

class GoalRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Save a new goal to database
    fun save(goal: Goal): Goal {
        val db = dbHelper.writableDatabase
        val id = GoalTable.insert(db, goal)
        db.close()
        return goal.copy(goalId = id.toInt())
    }

    // Get all goals from database
    fun getAllGoals(): List<Goal> {
        val db = dbHelper.readableDatabase
        val goals = GoalTable.getAll(db)
        db.close()
        return goals
    }

    // Get goals for a specific user
    fun getGoalsByUserId(userId: Int): List<Goal> {
        val db = dbHelper.readableDatabase
        val goals = GoalTable.getByUserId(db, userId)
        db.close()
        return goals
    }
}