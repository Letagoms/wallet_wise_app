// screens/ExpenseListActivity.kt
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
import com.example.wallet_wise_app.services.ExpenseService
import java.util.Locale

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var lvExpenses: ListView
    private lateinit var emptyText: TextView
    private lateinit var btnAddExpense: Button
    private lateinit var expenseService: ExpenseService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        lvExpenses = findViewById(R.id.lvExpenses)
        emptyText = findViewById(R.id.emptyText)
        btnAddExpense = findViewById(R.id.btnGoToAddExpense)
        expenseService = ExpenseService(this)

        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, CreateExpenseActivity::class.java))
        }

        loadExpenses()
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {
        Thread {
            try {
                val expenses = expenseService.getAllExpenses()

                runOnUiThread {
                    if (expenses.isEmpty()) {
                        lvExpenses.visibility = android.view.View.GONE
                        emptyText.visibility = android.view.View.VISIBLE
                    } else {
                        lvExpenses.visibility = android.view.View.VISIBLE
                        emptyText.visibility = android.view.View.GONE

                        val expenseStrings = expenses.map {
                            "${it.name} - R${String.format(Locale.US, "%.2f", it.amount)}\n" +
                                    "📅 ${it.date} | 🕐 ${it.startTime} - ${it.endTime}\n" +
                                    "📝 ${it.description}"
                        }

                        val adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1,
                            expenseStrings
                        )
                        lvExpenses.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}