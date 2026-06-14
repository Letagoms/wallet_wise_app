// This class acts as a BRIDGE between the Service layer and the Database layer
// It translates app operations into database operations
// Notice: NO business logic here - just pure database operations

package com.example.wallet_wise_app.repositories

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.database.ExpenseTable
import com.example.wallet_wise_app.models.Expense

// Repository takes Context to create DatabaseHelper
// Context = app information (needed to access database files)
class ExpenseRepository(private val context: Context) {

    // Creates the database helper instance
    // This is the ONLY place that directly talks to DatabaseHelper
    private val dbHelper = DatabaseHelper(context)

    // ========== SAVE (CREATE) ==========
    // Takes an Expense object (without an ID usually, or ID=0)
    // Saves it to database and returns the SAME expense with the new ID
    fun save(expense: Expense): Expense {
        // Get writable database connection
        val db = dbHelper.writableDatabase

        // Call ExpenseTable to do the actual SQL INSERT
        // Returns the auto-generated ID from database (e.g., 5)
        val id = ExpenseTable.insert(db, expense)

        // Close database connection to free resources
        db.close()

        // Return a COPY of the original expense WITH the new ID
        // copy() creates a new Expense object with same values but updated id
        // Example: expense had id=0, now returns expense with id=5
        return expense.copy(id = id.toInt())
    }

    // ========== GET ALL (READ) ==========
    // Retrieves ALL expenses from the database
    // No filtering, no sorting logic here - that would go in Service
    fun getAllExpenses(): List<Expense> {
        // Get readable database connection (faster for reading)
        val db = dbHelper.readableDatabase

        // Call ExpenseTable to run SELECT query
        val expenses = ExpenseTable.getAll(db)

        // Close connection
        db.close()

        // Return the list of expenses (empty list if none found)
        return expenses
    }

    // Other methods you could add:
    // fun getById(id: Int): Expense?  - Get single expense
    // fun update(expense: Expense): Boolean - Update existing expense
    // fun delete(id: Int): Boolean - Delete expense
}