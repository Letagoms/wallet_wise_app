// database/DatabaseHelper.kt
package com.example.wallet_wise_app.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wallet_wise_app.model.Expense
import com.example.wallet_wise_app.model.Goal
import com.example.wallet_wise_app.model.Category
import com.example.wallet_wise_app.model.User
import com.example.wallet_wise_app.model.ProjectedExpense
import com.example.wallet_wise_app.model.ProjectedIncome
import com.example.wallet_wise_app.model.Achievement
import com.example.wallet_wise_app.model.UserStats

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wallet_wise.db"
        private const val DATABASE_VERSION = 8

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        ExpenseTable.createTable(db)
        GoalTable.createTable(db)
        CategoryTable.createTable(db)
        UserTable.createTable(db)
        ProjectedExpenseTable.createTable(db)
        ProjectedIncomeTable.createTable(db)
        AchievementTable.createTable(db)
        UserStatsTable.createTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        ExpenseTable.dropTable(db)
        GoalTable.dropTable(db)
        CategoryTable.dropTable(db)
        UserTable.dropTable(db)
        ProjectedExpenseTable.dropTable(db)
        ProjectedIncomeTable.dropTable(db)
        AchievementTable.dropTable(db)
        UserStatsTable.dropTable(db)
        onCreate(db)
    }

    // ========== EXPENSE METHODS ==========
    fun insertExpense(expense: Expense): Long {
        val db = writableDatabase
        return ExpenseTable.insert(db, expense)
    }

    fun getAllExpenses(): List<Expense> {
        val db = readableDatabase
        return ExpenseTable.getAll(db)
    }

    // ========== GOAL METHODS ==========
    fun insertGoal(goal: Goal): Long {
        val db = writableDatabase
        return GoalTable.insert(db, goal)
    }

    fun getAllGoals(): List<Goal> {
        val db = readableDatabase
        return GoalTable.getAll(db)
    }

    fun getGoalsByUserId(userId: Int): List<Goal> {
        val db = readableDatabase
        return GoalTable.getByUserId(db, userId)
    }

    // ========== CATEGORY METHODS ==========
    fun insertCategory(category: Category): Long {
        val db = writableDatabase
        return CategoryTable.insert(db, category)
    }

    fun getAllCategories(): List<Category> {
        val db = readableDatabase
        return CategoryTable.getAll(db)
    }

    fun getCategoriesByUserId(userId: Int): List<Category> {
        val db = readableDatabase
        return CategoryTable.getByUserId(db, userId)
    }

    fun categoryExists(categoryName: String, userId: Int): Boolean {
        val db = readableDatabase
        return CategoryTable.exists(db, categoryName, userId)
    }

    fun deleteCategory(categoryId: Int): Boolean {
        val db = writableDatabase
        return CategoryTable.deleteById(db, categoryId)
    }

    // ========== USER METHODS ==========
    fun insertUser(user: User): Long {
        val db = writableDatabase
        return UserTable.insert(db, user)
    }

    fun getUserByUsername(username: String): User? {
        val db = readableDatabase
        return UserTable.getUserByUsername(db, username)
    }

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        return UserTable.getUserByEmail(db, email)
    }

    fun usernameExists(username: String): Boolean {
        val db = readableDatabase
        return UserTable.usernameExists(db, username)
    }

    fun emailExists(email: String): Boolean {
        val db = readableDatabase
        return UserTable.emailExists(db, email)
    }

    // ========== PROJECTED EXPENSE METHODS ==========
    fun insertProjectedExpense(expense: ProjectedExpense): Long {
        val db = writableDatabase
        return ProjectedExpenseTable.insert(db, expense)
    }

    fun getAllProjectedExpenses(): List<ProjectedExpense> {
        val db = readableDatabase
        return ProjectedExpenseTable.getAll(db)
    }

    fun getProjectedExpensesByUserId(userId: Int): List<ProjectedExpense> {
        val db = readableDatabase
        return ProjectedExpenseTable.getByUserId(db, userId)
    }

    fun getProjectedExpensesByMonth(userId: Int, year: Int, month: Int): List<ProjectedExpense> {
        val db = readableDatabase
        return ProjectedExpenseTable.getByMonth(db, userId, year, month)
    }

    fun deleteProjectedExpense(id: Int): Boolean {
        val db = writableDatabase
        return ProjectedExpenseTable.delete(db, id)
    }

    // ========== PROJECTED INCOME METHODS ==========
    fun insertProjectedIncome(income: ProjectedIncome): Long {
        val db = writableDatabase
        return ProjectedIncomeTable.insert(db, income)
    }

    fun getAllProjectedIncomes(): List<ProjectedIncome> {
        val db = readableDatabase
        return ProjectedIncomeTable.getAll(db)
    }

    fun getProjectedIncomesByUserId(userId: Int): List<ProjectedIncome> {
        val db = readableDatabase
        return ProjectedIncomeTable.getByUserId(db, userId)
    }

    fun getProjectedIncomesByMonth(userId: Int, year: Int, month: Int): List<ProjectedIncome> {
        val db = readableDatabase
        return ProjectedIncomeTable.getByMonth(db, userId, year, month)
    }

    fun deleteProjectedIncome(id: Int): Boolean {
        val db = writableDatabase
        return ProjectedIncomeTable.delete(db, id)
    }

    // ========== ACHIEVEMENT METHODS ==========
    fun insertAchievement(achievement: Achievement): Long {
        val db = writableDatabase
        return AchievementTable.insert(db, achievement)
    }

    fun getAchievementsByUserId(userId: Int): List<Achievement> {
        val db = readableDatabase
        return AchievementTable.getAllByUserId(db, userId)
    }

    fun updateAchievementUnlocked(achievementId: Int, unlockedAt: String) {
        val db = writableDatabase
        AchievementTable.updateUnlocked(db, achievementId, unlockedAt)
    }

    fun getAchievementByRequirement(userId: Int, requirement: String): Achievement? {
        val db = readableDatabase
        return AchievementTable.getByRequirement(db, userId, requirement)
    }

    // ========== USER STATS METHODS ==========
    fun getUserStats(userId: Int): UserStats? {
        val db = readableDatabase
        return UserStatsTable.getByUserId(db, userId)
    }

    fun updateUserStats(stats: UserStats) {
        val db = writableDatabase
        UserStatsTable.updateLoginCount(db, stats.userId, stats.loginCount, stats.lastLoginDate ?: "")
    }

    fun updateUserExpenseCount(userId: Int, count: Int) {
        val db = writableDatabase
        UserStatsTable.updateExpenseCount(db, userId, count)
    }

    fun updateUserGoalCount(userId: Int, count: Int) {
        val db = writableDatabase
        UserStatsTable.updateGoalCount(db, userId, count)
    }

    fun createOrUpdateUserStats(stats: UserStats) {
        val db = writableDatabase
        if (getUserStats(stats.userId) == null) {
            UserStatsTable.insert(db, stats)
        } else {
            UserStatsTable.updateLoginCount(db, stats.userId, stats.loginCount, stats.lastLoginDate ?: "")
        }
    }
}