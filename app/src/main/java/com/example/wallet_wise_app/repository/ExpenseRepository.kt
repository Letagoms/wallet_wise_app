// repositories/ExpenseRepository.kt
package com.example.wallet_wise_app.repositories

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.models.Expense

class ExpenseRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun save(expense: Expense): Expense {
        val id = dbHelper.insertExpense(
            name = expense.name,
            amount = expense.amount,
            date = expense.date,
            startTime = expense.startTime,
            endTime = expense.endTime,
            photo = expense.photo,
            description = expense.description
        )
        return expense.copy(id = id.toInt())
    }

    fun getAllExpenses(): List<Expense> {
        return dbHelper.getAllExpenses()
    }
}