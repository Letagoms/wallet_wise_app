package com.example.wallet_wise_app

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.databinding.ActivityExpenseListBinding
import java.io.File

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        loadExpenses()

        binding.btnGoToAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    // Reads all expenses from the database and displays them in the ListView
    private fun loadExpenses() {
        // Get all expenses sorted by newest first
        val expenses = dbHelper.getAllExpenses()

        // Create a list of strings to display in the ListView
        val displayList = mutableListOf<String>()

        // Loop through each expense and format it for display
        for (expense in expenses) {
            // Check if this expense has a receipt attached
            val receiptIndicator = if (!expense.receiptPath.isNullOrEmpty()) " 📎" else ""

            // Format: "Description 📎"
            //         "Category • Time"
            //         "-R Amount"
            displayList.add("${expense.description}$receiptIndicator\n${expense.category} • ${expense.time}\n-R${String.format("%.2f", expense.amount)}")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
        binding.lvExpenses.adapter = adapter

        binding.lvExpenses.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val expense = expenses[position]

            if (!expense.receiptPath.isNullOrEmpty()) {
                val file = File(expense.receiptPath)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(expense.receiptPath)
                    showReceiptDialog(bitmap)
                } else {
                    Toast.makeText(this, "Receipt file not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No receipt for this expense", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showReceiptDialog(bitmap: android.graphics.Bitmap) {
        val builder = AlertDialog.Builder(this)
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)
        imageView.adjustViewBounds = true
        imageView.maxHeight = 1200
        builder.setView(imageView)
        builder.setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}