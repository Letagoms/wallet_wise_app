// screens/ExpenseListActivity.kt
// This screen shows ALL saved expenses in a scrollable list
// User can see what they've spent and tap "Add Expense" to create new ones
// Called from CreateExpenseActivity when user clicks "View All Expenses"

package com.example.wallet_wise_app.screens

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.database.DatabaseHelper
import java.util.Locale

class ExpenseListActivity : AppCompatActivity() {

    // ========== UI COMPONENTS ==========
    private lateinit var lvExpenses: ListView      // Scrollable list that displays expenses
    private lateinit var emptyText: TextView       // Shows "No expenses" message when list is empty
    private lateinit var btnAddExpense: Button     // Button to go to Add Expense screen
    private lateinit var dbHelper: DatabaseHelper  // Helper to read expenses from database

    // ========== ON CREATE - SCREEN STARTUP ==========
    // Called ONCE when this screen is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)  // Load the XML layout for this screen

        // Connect UI variables to actual XML views
        lvExpenses = findViewById(R.id.lvExpenses)
        emptyText = findViewById(R.id.emptyText)
        btnAddExpense = findViewById(R.id.btnGoToAddExpense)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // ========== ADD EXPENSE BUTTON ==========
        // When clicked, navigate to the CreateExpenseActivity screen
        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, CreateExpenseActivity::class.java))
        }

        // Load and display all expenses
        loadExpenses()
    }

    // ========== ON RESUME ==========
    // Called every time the screen becomes visible
    // This includes:
    //   - First time opening the screen
    //   - Coming BACK from Add Expense screen (after saving a new expense)
    // This ensures the list is ALWAYS up to date
    override fun onResume() {
        super.onResume()
        loadExpenses()  // Refresh the list (shows newly added expenses)
    }

    // ========== LOAD EXPENSES FROM DATABASE ==========
    // Runs in background thread (Thread) so UI doesn't freeze while querying database
    private fun loadExpenses() {
        // Thread { } runs code in background (not blocking UI)
        Thread {
            // Get ALL expenses from database (returns List<Expense>)
            val expenses = dbHelper.getAllExpenses()

            // Switch back to UI thread to update the screen
            // (Cannot update UI from background thread)
            runOnUiThread {
                // ========== CASE 1: NO EXPENSES ==========
                if (expenses.isEmpty()) {
                    // Hide the list view
                    lvExpenses.visibility = android.view.View.GONE
                    // Show the "empty" message
                    emptyText.visibility = android.view.View.VISIBLE
                }
                // ========== CASE 2: HAS EXPENSES ==========
                else {
                    // Show the list view
                    lvExpenses.visibility = android.view.View.VISIBLE
                    // Hide the empty message
                    emptyText.visibility = android.view.View.GONE

                    // Convert each Expense object into a displayable string
                    // .map { } transforms each expense into a formatted string
                    val expenseStrings = expenses.map { expense ->
                        // Handle empty category (show "No category" instead of blank)
                        val categoryText = if (expense.category.isBlank()) "No category" else expense.category

                        // Build the display string with emojis for visual clarity
                        // Line 1: Name and amount
                        // Line 2: Category, Date, Time range
                        // Line 3: Description
                        "${expense.name} - R${String.format(Locale.US, "%.2f", expense.amount)}\n" +
                                "📁 $categoryText | 📅 ${expense.date} | 🕐 ${expense.startTime} - ${expense.endTime}\n" +
                                "📝 ${expense.description}"
                    }

                    // ArrayAdapter is Android's built-in adapter to connect data to ListView
                    // android.R.layout.simple_list_item_1 = default Android text layout
                    val adapter = ArrayAdapter(
                        this,                           // Context (this screen)
                        android.R.layout.simple_list_item_1,  // How each item looks (simple text)
                        expenseStrings                  // The data to display
                    )

                    // Attach the adapter to the ListView
                    lvExpenses.adapter = adapter
                }
            }
        }.start()  // Start the background thread
    }
}