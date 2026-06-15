// screens/ExpenseListActivity.kt
package com.example.wallet_wise_app.screens

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.model.Expense
import com.example.wallet_wise_app.utils.DateRangePickerDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var lvExpenses: ListView
    private lateinit var emptyText: TextView
    private lateinit var btnAddExpense: Button
    private lateinit var btnMenu: ImageButton
    private lateinit var btnCalendar: ImageButton
    private lateinit var btnClearFilter: Button
    private lateinit var tvDateRange: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navExpenseList: Button
    private lateinit var navViewGoals: Button
    private lateinit var navSetGoals: Button
    private lateinit var navCreateCategory: Button
    private lateinit var navViewCategories: Button
    private lateinit var navProjectionCalendar: Button
    private lateinit var dbHelper: DatabaseHelper

    private var allExpenses: List<Expense> = emptyList()
    private var filteredExpenses: List<Expense> = emptyList()
    private var startDateFilter: String? = null
    private var endDateFilter: String? = null

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        dbHelper = DatabaseHelper.getInstance(this)

        lvExpenses = findViewById(R.id.lvExpenses)
        emptyText = findViewById(R.id.emptyText)
        btnAddExpense = findViewById(R.id.btnGoToAddExpense)
        btnMenu = findViewById(R.id.btnMenu)
        btnCalendar = findViewById(R.id.btnCalendar)
        btnClearFilter = findViewById(R.id.btnClearFilter)
        tvDateRange = findViewById(R.id.tvDateRange)
        drawerLayout = findViewById(R.id.drawerLayout)
        navExpenseList = findViewById(R.id.navExpenseList)
        navViewGoals = findViewById(R.id.navViewGoals)
        navSetGoals = findViewById(R.id.navSetGoals)
        navCreateCategory = findViewById(R.id.navCreateCategory)
        navViewCategories = findViewById(R.id.navViewCategories)
        navProjectionCalendar = findViewById(R.id.navProjectionCalendar)

        setupDrawer()
        loadExpenses()

        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, CreateExpenseActivity::class.java))
        }

        btnCalendar.setOnClickListener {
            showDateRangePicker()
        }

        btnClearFilter.setOnClickListener {
            clearFilter()
        }
    }

    private fun setupDrawer() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navExpenseList.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navViewGoals.setOnClickListener {
            startActivity(Intent(this, ViewGoalsActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navSetGoals.setOnClickListener {
            startActivity(Intent(this, SetGoalsActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navCreateCategory.setOnClickListener {
            startActivity(Intent(this, CreateCategoryActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navViewCategories.setOnClickListener {
            startActivity(Intent(this, ViewCategoriesActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navProjectionCalendar.setOnClickListener {
            startActivity(Intent(this, ProjectionCalendarActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun showDateRangePicker() {
        DateRangePickerDialog(this) { startDate, endDate ->
            startDateFilter = startDate
            endDateFilter = endDate
            applyFilter()
        }.show()
    }

    private fun applyFilter() {
        if (startDateFilter != null && endDateFilter != null) {
            filteredExpenses = allExpenses.filter { expense ->
                expense.date >= startDateFilter!! && expense.date <= endDateFilter!!
            }
            tvDateRange.text = "Showing: ${startDateFilter} to ${endDateFilter}"
            btnClearFilter.visibility = android.view.View.VISIBLE
            displayExpenses(filteredExpenses)
        }
    }

    private fun clearFilter() {
        startDateFilter = null
        endDateFilter = null
        tvDateRange.text = "Showing all expenses"
        btnClearFilter.visibility = android.view.View.GONE
        displayExpenses(allExpenses)
    }

    private fun displayExpenses(expenses: List<Expense>) {
        if (expenses.isEmpty()) {
            lvExpenses.visibility = android.view.View.GONE
            emptyText.visibility = android.view.View.VISIBLE
            emptyText.text = if (startDateFilter != null) "No expenses found for selected dates" else "No expenses yet. Add your first expense!"
        } else {
            lvExpenses.visibility = android.view.View.VISIBLE
            emptyText.visibility = android.view.View.GONE

            val expenseStrings = expenses.map {
                val hasPhoto = if (it.photo.isNotBlank()) "📷 " else ""
                "$hasPhoto${it.name} - R${String.format(Locale.US, "%.2f", it.amount)}\n" +
                        "📁 ${it.category} | 📅 ${it.date} | 🕐 ${it.startTime} - ${it.endTime}\n" +
                        "📝 ${it.description}"
            }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                expenseStrings
            )
            lvExpenses.adapter = adapter

            lvExpenses.setOnItemClickListener { _, _, position, _ ->
                val expense = expenses[position]
                if (expense.photo.isNotBlank()) {
                    showImagePreview(expense.photo, expense.name)
                } else {
                    Toast.makeText(this, "No photo attached to this expense", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {
        Thread {
            allExpenses = dbHelper.getAllExpenses()

            runOnUiThread {
                if (startDateFilter != null && endDateFilter != null) {
                    applyFilter()
                } else {
                    displayExpenses(allExpenses)
                }
            }
        }.start()
    }

    private fun showImagePreview(photoPath: String, expenseName: String) {
        val imageFile = File(photoPath)
        if (!imageFile.exists()) {
            Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_preview, null)
        val ivFullImage = dialogView.findViewById<ImageView>(R.id.ivFullImage)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)

        val bitmap = BitmapFactory.decodeFile(photoPath)
        ivFullImage.setImageBitmap(bitmap)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}