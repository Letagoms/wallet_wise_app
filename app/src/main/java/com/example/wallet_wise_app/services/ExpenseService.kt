// services/ExpenseService.kt
package com.example.wallet_wise_app.services

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.model.Expense

class ExpenseService(private val context: Context) {

    private val dbHelper = DatabaseHelper.getInstance(context)

    fun addExpense(
        name: String,
        amount: Double,
        category: String,
        date: String,
        startTime: String,
        endTime: String,
        photo: String,
        description: String
    ): Expense {
        if (name.isBlank()) {
            throw Exception("Name is required")
        }

        if (amount <= 0) {
            throw Exception("Amount must be greater than 0")
        }

        if (date.isBlank()) {
            throw Exception("Date is required")
        }

        val expense = Expense(
            id = 0,
            name = name,
            amount = amount,
            category = category,
            date = date,
            startTime = startTime,
            endTime = endTime,
            photo = photo,
            description = description
        )

        val id = dbHelper.insertExpense(expense)
        return expense.copy(id = id.toInt())
    }

    fun getAllExpenses(): List<Expense> {
        return dbHelper.getAllExpenses()
    }

    fun getExpensesByCategory(category: String): List<Expense> {
        return dbHelper.getAllExpenses().filter { it.category.equals(category, ignoreCase = true) }
    }

    fun getTotalSpent(): Double {
        return dbHelper.getAllExpenses().sumOf { it.amount }
    }

    fun getTotalByCategory(category: String): Double {
        return dbHelper.getAllExpenses()
            .filter { it.category.equals(category, ignoreCase = true) }
            .sumOf { it.amount }
    }
}