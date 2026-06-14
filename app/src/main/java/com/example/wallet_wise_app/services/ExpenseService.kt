// services/ExpenseService.kt
// This class contains ALL the BUSINESS LOGIC and RULES of your app
// It's the "brain" - decides WHAT can be done, not HOW it's done
// Notice: NO database code here (no SQL, no table names, no cursors)

package com.example.wallet_wise_app.services

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.models.Expense

// Service takes Context to access database through Repository or DatabaseHelper
class ExpenseService(private val context: Context) {

    // Directly using DatabaseHelper (could also use Repository pattern)
    // For learning, this is fine. In larger apps, Service would call Repository
    private val dbHelper = DatabaseHelper(context)

    // ========== ADD EXPENSE (CREATE with validation) ==========
    // This is the most important method - it ENFORCES business rules
    // Takes individual fields, validates them, then creates and saves an Expense
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

        // ========== BUSINESS RULES (VALIDATION) ==========
        // These are the RULES of your budget app
        // They decide what is ALLOWED and what is NOT

        // RULE 1: Name cannot be empty
        if (name.isBlank()) {
            throw Exception("Name is required")  // Stop execution, tell user
        }

        // RULE 2: Amount must be positive (can't spend negative money)
        if (amount <= 0) {
            throw Exception("Amount must be greater than 0")
        }

        // RULE 3: Date cannot be empty
        if (date.isBlank()) {
            throw Exception("Date is required")
        }

        // RULE 4: (Could add more - e.g., amount < 10000, valid date format, etc.)

        // After ALL rules pass, create the Expense object
        // id = 0 means "new expense" - database will generate real ID
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

        // Save to database (this is the "HOW" - delegated to DatabaseHelper)
        val id = dbHelper.insertExpense(expense)

        // Return the expense with its new database ID
        return expense.copy(id = id.toInt())
    }

    // ========== GET ALL EXPENSES (READ) ==========
    // Simple pass-through - just asks database for all expenses
    // No business rules here because retrieval doesn't need validation
    fun getAllExpenses(): List<Expense> {
        return dbHelper.getAllExpenses()
    }

    // ========== FILTER BY CATEGORY (BUSINESS LOGIC) ==========
    // This IS business logic - filtering data based on category
    // Gets ALL expenses, then filters them in memory
    // ignoreCase = true means "Food" matches "food" or "FOOD"
    fun getExpensesByCategory(category: String): List<Expense> {
        return dbHelper.getAllExpenses().filter {
            it.category.equals(category, ignoreCase = true)
        }
    }

    // ========== CALCULATE TOTAL SPENT (BUSINESS LOGIC) ==========
    // This is a CALCULATION - sum of all expense amounts
    // sumOf is a Kotlin function that adds all amounts together
    fun getTotalSpent(): Double {
        return dbHelper.getAllExpenses().sumOf { it.amount }
    }

    // ========== CALCULATE TOTAL BY CATEGORY (BUSINESS LOGIC) ==========
    // More complex business logic - filter THEN sum
    // Example: How much did I spend on "Food" this month?
    fun getTotalByCategory(category: String): Double {
        return dbHelper.getAllExpenses()
            .filter { it.category.equals(category, ignoreCase = true) }  // First filter by category
            .sumOf { it.amount }  // Then add up all amounts
    }
}