// services/ExpenseService.kt
package com.example.wallet_wise_app.services

import android.content.Context
import com.example.wallet_wise_app.models.Expense
import com.example.wallet_wise_app.repositories.ExpenseRepository

class ExpenseService(private val context: Context) {

    private val repository = ExpenseRepository(context)

    fun addExpense(
        name: String,
        amount: Double,
        date: String,
        startTime: String,
        endTime: String,
        photo: String,
        description: String
    ): Expense {
        if (name.isBlank()) throw Exception("Name is required")
        if (amount <= 0) throw Exception("Amount must be greater than 0")
        if (date.isBlank()) throw Exception("Date is required")

        val expense = Expense(
            id = 0,
            name = name,
            amount = amount,
            date = date,
            startTime = startTime,
            endTime = endTime,
            photo = photo,
            description = description
        )

        return repository.save(expense)
    }

    fun getAllExpenses(): List<Expense> {
        return repository.getAllExpenses()
    }
}