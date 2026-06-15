// services/GamificationService.kt
package com.example.wallet_wise_app.services

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.model.Achievement
import com.example.wallet_wise_app.model.UserStats
import java.text.SimpleDateFormat
import java.util.*

class GamificationService(private val context: Context) {

    private val dbHelper = DatabaseHelper.getInstance(context)

    // Predefined achievements
    private val defaultAchievements = listOf(
        Achievement(
            name = "Consistent Logger",
            description = "Logged in 10 times",
            requirement = "login_count",
            requiredValue = 10,
            icon = "🔥",
            userId = 0
        ),
        Achievement(
            name = "Expense Tracker",
            description = "Logged 10 expenses",
            requirement = "expense_count",
            requiredValue = 10,
            icon = "📊",
            userId = 0
        ),
        Achievement(
            name = "Goal Setter",
            description = "Set 3 financial goals",
            requirement = "goal_count",
            requiredValue = 3,
            icon = "🎯",
            userId = 0
        )
    )

    // Initialize achievements for a new user
    fun initializeAchievements(userId: Int) {
        val existingAchievements = dbHelper.getAchievementsByUserId(userId)
        if (existingAchievements.isEmpty()) {
            defaultAchievements.forEach { achievement ->
                dbHelper.insertAchievement(achievement.copy(userId = userId))
            }
        }
    }

    // Initialize user stats for a new user
    fun initializeUserStats(userId: Int) {
        if (dbHelper.getUserStats(userId) == null) {
            val stats = UserStats(
                userId = userId,
                loginCount = 1,
                expenseCount = 0,
                goalCount = 0,
                lastLoginDate = getCurrentDate()
            )
            dbHelper.createOrUpdateUserStats(stats)
        }
    }

    // Record a login and check for achievement
    fun recordLogin(userId: Int): String? {
        var stats = dbHelper.getUserStats(userId)
        val today = getCurrentDate()

        if (stats == null) {
            stats = UserStats(userId = userId, loginCount = 1, lastLoginDate = today)
            dbHelper.createOrUpdateUserStats(stats)
        } else {
            // Only increment if last login was a different day
            if (stats.lastLoginDate != today) {
                val newCount = stats.loginCount + 1
                dbHelper.updateUserStats(stats.copy(loginCount = newCount, lastLoginDate = today))
                stats = stats.copy(loginCount = newCount, lastLoginDate = today)
            }
        }

        return checkAndUnlockAchievement(userId, "login_count", stats.loginCount)
    }

    // Record an expense and check for achievement
    fun recordExpense(userId: Int): String? {
        val stats = dbHelper.getUserStats(userId)
        val newCount = (stats?.expenseCount ?: 0) + 1
        dbHelper.updateUserExpenseCount(userId, newCount)

        return checkAndUnlockAchievement(userId, "expense_count", newCount)
    }

    // Record a goal and check for achievement
    fun recordGoal(userId: Int): String? {
        val stats = dbHelper.getUserStats(userId)
        val newCount = (stats?.goalCount ?: 0) + 1
        dbHelper.updateUserGoalCount(userId, newCount)

        return checkAndUnlockAchievement(userId, "goal_count", newCount)
    }

    // Check if an achievement should be unlocked
    private fun checkAndUnlockAchievement(userId: Int, requirement: String, currentValue: Int): String? {
        val achievement = dbHelper.getAchievementByRequirement(userId, requirement)

        if (achievement != null && !achievement.isUnlocked && currentValue >= achievement.requiredValue) {
            // Unlock the achievement
            dbHelper.updateAchievementUnlocked(achievement.id, getCurrentDate())
            return achievement.name
        }
        return null
    }

    // Get all achievements for a user (with unlock status)
    fun getUserAchievements(userId: Int): List<Achievement> {
        return dbHelper.getAchievementsByUserId(userId)
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}