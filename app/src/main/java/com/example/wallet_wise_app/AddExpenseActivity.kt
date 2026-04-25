package com.example.wallet_wise_app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.databinding.ActivityAddExpenseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Set up category dropdown
        val categories = arrayOf(
            "Select category",
            "Groceries",
            "Dining",
            "Transport",
            "Entertainment",
            "Housing",
            "Shopping",
            "Healthcare",
            "Other"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        // Set current date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val today = Date()
        binding.tvDate.text = displayDateFormat.format(today)

        // Date picker click
        binding.tvDate.setOnClickListener {
            // We'll add date picker dialog later
        }

        // Add Receipt click
        binding.btnAddReceipt.setOnClickListener {
            Toast.makeText(this, "Receipt upload coming soon", Toast.LENGTH_SHORT).show()
        }

        // Add Expense button click
        binding.btnAddExpense.setOnClickListener {
            saveExpense(dateFormat.format(today))
        }
    }

    private fun saveExpense(currentDate: String) {
        val amountText = binding.etAmount.text.toString().replace("R", "").trim()

        if (amountText.isEmpty() || amountText == "0.00") {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            return
        }

        val category = binding.spinnerCategory.selectedItem.toString()
        if (category == "Select category") {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())

        val expense = Expense(
            amount = amountText.toDouble(),
            date = currentDate,
            time = currentTime,
            category = category,
            description = category  // You can make this more specific later
        )

        val result = dbHelper.insertExpense(expense)
        if (result != -1L) {
            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show()
            finish()  // Go back to list screen
        } else {
            Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show()
        }
    }
}