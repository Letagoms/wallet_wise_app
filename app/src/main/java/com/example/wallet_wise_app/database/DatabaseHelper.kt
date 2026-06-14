// database/DatabaseHelper.kt
// This class manages the SQLite database - creates, upgrades, and provides access

package com.example.wallet_wise_app.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wallet_wise_app.models.Expense

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wallet_wise.db"
        private const val DATABASE_VERSION = 4
    }

    // In DatabaseHelper.kt, add this line to onCreate():
    override fun onCreate(db: SQLiteDatabase) {
        ExpenseTable.createTable(db)
        GoalTable.createTable(db)  // ← ADD THIS LINE
    }

    // In onUpgrade(), add:
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        ExpenseTable.dropTable(db)
        GoalTable.dropTable(db)  // ← ADD THIS LINE
        onCreate(db)
    }

    // Public method to save an expense to the database
    // Takes an Expense object (from models/Expense.kt) and returns the auto-generated ID
    fun insertExpense(expense: Expense): Long {
        // writableDatabase = allows reading AND writing
        val db = writableDatabase
        // Delegate the actual insert operation to ExpenseTable
        val id = ExpenseTable.insert(db, expense)
        // Always close the database connection to free resources
        db.close()
        return id
    }

    // Public method to retrieve all expenses from the database
    fun getAllExpenses(): List<Expense> {
        // readableDatabase = allows only reading
        val db = readableDatabase
        // Delegate the query to ExpenseTable
        val expenses = ExpenseTable.getAll(db)
        // Close the connection
        db.close()
        return expenses
    }
}