package com.example.wallet_wise_app

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)

        if (userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        loadExpenses()

        binding.btnGoToAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("SELECTED_DATE", selectedDate)
            startActivity(intent)
        }

        binding.btnCalendar.setOnClickListener {
            showDatePicker()
        }

        setupBottomNavigation()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val calendarSelected = Calendar.getInstance()
            calendarSelected.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormat.format(calendarSelected.time)
            
            Toast.makeText(this, "Showing expenses for: $selectedDate", Toast.LENGTH_SHORT).show()
            loadExpenses(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    selectedDate = null // Reset filter when clicking home
                    loadExpenses()
                    true
                }
                R.id.nav_budget -> {
                    val intent = Intent(this, Budget::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpenses(selectedDate)
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun loadExpenses(dateFilter: String? = null) {
        val expenses = if (dateFilter == null) {
            dbHelper.getAllExpenses(userId)
        } else {
            dbHelper.getExpensesByDate(userId, dateFilter)
        }

        val displayList = mutableListOf<String>()

        for (expense in expenses) {
            val receiptIndicator = if (!expense.receiptPath.isNullOrEmpty()) " 📎" else ""
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