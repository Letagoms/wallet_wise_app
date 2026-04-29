package com.example.wallet_wise_app

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.databinding.ActivityExpenseListBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private lateinit var dbHelper: ExpenseDatabaseHelper
    private var userId: Int = -1
    private var currentStartDate: String? = null
    private var currentEndDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ExpenseDatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)

        loadExpenses()

        binding.btnGoToAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        // Hamburger menu click
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }

        // Navigation item click
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(binding.navigationView)
            true
        }

        // Calendar icon click
        binding.btnCalendar.setOnClickListener {
            showDateRangeDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {
        val expenses = if (currentStartDate != null && currentEndDate != null) {
            dbHelper.getExpensesByDateRange(currentStartDate!!, currentEndDate!!, userId)
        } else {
            dbHelper.getAllExpenses(userId)
        }

        val displayList = mutableListOf<String>()

        for (expense in expenses) {
            val receiptIndicator = if (!expense.receiptPath.isNullOrEmpty()) " 📎" else ""
            displayList.add("${expense.description}$receiptIndicator\n${expense.category} • ${expense.time}\n-R${String.format(Locale.getDefault(), "%.2f", expense.amount)}")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
        binding.lvExpenses.adapter = adapter

        binding.lvExpenses.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val expense = expenses[position]
            if (!expense.receiptPath.isNullOrEmpty()) {
                val file = File(expense.receiptPath)
                if (file.exists()) {
                    showReceiptDialog(BitmapFactory.decodeFile(expense.receiptPath))
                } else {
                    Toast.makeText(this, "Receipt file not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No receipt for this expense", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDateRangeDialog() {
        val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null) as TextView
        // We'll use a custom layout instead
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Date Range")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val tvStartDate = TextView(this).apply {
            text = "Start Date: Tap to select"
            textSize = 16f
            setPadding(0, 8, 0, 16)
        }

        val tvEndDate = TextView(this).apply {
            text = "End Date: Tap to select"
            textSize = 16f
            setPadding(0, 8, 0, 24)
        }

        var startDate = ""
        var endDate = ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        tvStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val cal = Calendar.getInstance().apply { set(year, month, day) }
                startDate = dateFormat.format(cal.time)
                tvStartDate.text = "Start Date: $startDate"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        tvEndDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val cal = Calendar.getInstance().apply { set(year, month, day) }
                endDate = dateFormat.format(cal.time)
                tvEndDate.text = "End Date: $endDate"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        layout.addView(tvStartDate)
        layout.addView(tvEndDate)

        builder.setView(layout)

        builder.setPositiveButton("Apply") { _, _ ->
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                currentStartDate = startDate
                currentEndDate = endDate
                loadExpenses()
            } else {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNeutralButton("Reset") { _, _ ->
            currentStartDate = null
            currentEndDate = null
            loadExpenses()
            Toast.makeText(this, "Showing all expenses", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showReceiptDialog(bitmap: android.graphics.Bitmap) {
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)
        imageView.adjustViewBounds = true
        imageView.maxHeight = 1200
        AlertDialog.Builder(this)
            .setView(imageView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}