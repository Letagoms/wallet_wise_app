// database/DatabaseHelper.kt
package com.example.wallet_wise_app.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wallet_wise_app.model.Expense
import com.example.wallet_wise_app.model.Goal
import com.example.wallet_wise_app.model.Category

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wallet_wise.db"
        private const val DATABASE_VERSION = 6

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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        ExpenseTable.dropTable(db)
        GoalTable.dropTable(db)
        CategoryTable.dropTable(db)
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
}