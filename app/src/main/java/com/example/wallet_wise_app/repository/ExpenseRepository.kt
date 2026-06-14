// repositories/ExpenseRepository.kt
package com.example.wallet_wise_app.repositories

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.database.ExpenseTable
import com.example.wallet_wise_app.models.Expense

class ExpenseRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun save(expense: Expense): Expense {
        val db = dbHelper.writableDatabase
        val id = ExpenseTable.insert(db, expense)
        db.close()
        return expense.copy(id = id.toInt())
    }

    fun getAllExpenses(): List<Expense> {
        val db = dbHelper.readableDatabase
        val expenses = ExpenseTable.getAll(db)
        db.close()
        return expenses
    }

    
}