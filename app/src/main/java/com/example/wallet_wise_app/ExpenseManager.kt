package com.example.wallet_wise_app

import android.content.Context

class ExpenseManager(context: Context) {

    private val dbHelper = ExpenseDatabaseHelper(context)

    fun addExpense(expense: Expense): Long {
        return dbHelper.insertExpense(expense)
    }

    fun getExpenses(userId: Int): List<Expense> {
        return dbHelper.getAllExpenses(userId)
    }

    fun getExpensesByDateRange(startDate: String, endDate: String, userId: Int): List<Expense> {
        return dbHelper.getExpensesByDateRange(startDate, endDate, userId)
    }

    fun getSpendingByCategory(startDate: String, endDate: String, userId: Int): Map<Int, Double> {
        return dbHelper.getSpendingByCategory(startDate, endDate, userId)
    }
}