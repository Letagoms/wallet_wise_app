// database/DatabaseHelper.kt
package com.example.wallet_wise_app.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wallet_wise_app.models.Expense

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wallet_wise.db"
        private const val DATABASE_VERSION = 3  // Increased to add category column
    }

    override fun onCreate(db: SQLiteDatabase) {
        ExpenseTable.createTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        ExpenseTable.dropTable(db)
        onCreate(db)
    }

    fun insertExpense(expense: Expense): Long {
        val db = writableDatabase
        val id = ExpenseTable.insert(db, expense)
        db.close()
        return id
    }

    fun getAllExpenses(): List<Expense> {
        val db = readableDatabase
        val expenses = ExpenseTable.getAll(db)
        db.close()
        return expenses
    }
}