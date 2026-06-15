// screens/ViewCategoriesActivity.kt
package com.example.wallet_wise_app.screens

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.model.Expense
import com.example.wallet_wise_app.services.CategoryService
import com.example.wallet_wise_app.utils.DateRangePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class ViewCategoriesActivity : AppCompatActivity() {

    private lateinit var lvCategories: ListView
    private lateinit var emptyText: TextView
    private lateinit var tvDateRange: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var btnClearFilter: Button
    private lateinit var btnCreateCategory: Button
    private lateinit var btnMenu: ImageButton
    private lateinit var btnCalendar: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navExpenseList: Button
    private lateinit var navViewGoals: Button
    private lateinit var navSetGoals: Button
    private lateinit var navCreateCategory: Button
    private lateinit var navViewCategories: Button
    private lateinit var navProjectionCalendar: Button
    private lateinit var categoryService: CategoryService
    private lateinit var dbHelper: DatabaseHelper

    private val currentUserId = 1
    private var startDateFilter: String? = null
    private var endDateFilter: String? = null
    private var allExpenses: List<Expense> = emptyList()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_categories)

        categoryService = CategoryService(this)
        dbHelper = DatabaseHelper.getInstance(this)

        lvCategories = findViewById(R.id.lvCategories)
        emptyText = findViewById(R.id.emptyText)
        tvDateRange = findViewById(R.id.tvDateRange)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        btnClearFilter = findViewById(R.id.btnClearFilter)
        btnCreateCategory = findViewById(R.id.btnCreateCategory)
        btnMenu = findViewById(R.id.btnMenu)
        btnCalendar = findViewById(R.id.btnCalendar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navExpenseList = findViewById(R.id.navExpenseList)
        navViewGoals = findViewById(R.id.navViewGoals)
        navSetGoals = findViewById(R.id.navSetGoals)
        navCreateCategory = findViewById(R.id.navCreateCategory)
        navViewCategories = findViewById(R.id.navViewCategories)
        navProjectionCalendar = findViewById(R.id.navProjectionCalendar)

        setupDrawer()

        btnCreateCategory.setOnClickListener {
            startActivity(Intent(this, CreateCategoryActivity::class.java))
        }

        btnCalendar.setOnClickListener {
            showDateRangePicker()
        }

        btnClearFilter.setOnClickListener {
            clearFilter()
        }

        loadData()
    }

    private fun setupDrawer() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navExpenseList.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
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
            val filteredExpenses = allExpenses.filter { expense ->
                expense.date >= startDateFilter!! && expense.date <= endDateFilter!!
            }
            tvDateRange.text = "Showing: ${startDateFilter} to ${endDateFilter}"
            btnClearFilter.visibility = android.view.View.VISIBLE
            displayCategories(filteredExpenses)
        }
    }

    private fun clearFilter() {
        startDateFilter = null
        endDateFilter = null
        tvDateRange.text = "Showing all time"
        btnClearFilter.visibility = android.view.View.GONE
        displayCategories(allExpenses)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        Thread {
            allExpenses = dbHelper.getAllExpenses()

            runOnUiThread {
                if (startDateFilter != null && endDateFilter != null) {
                    val filteredExpenses = allExpenses.filter { expense ->
                        expense.date >= startDateFilter!! && expense.date <= endDateFilter!!
                    }
                    displayCategories(filteredExpenses)
                } else {
                    displayCategories(allExpenses)
                }
            }
        }.start()
    }

    private fun displayCategories(expenses: List<Expense>) {
        val totalSpent = expenses.sumOf { it.amount }
        tvTotalSpent.text = "Total Spent: R${String.format(Locale.US, "%.2f", totalSpent)}"

        try {
            // Get categories with spending totals for the filtered expenses
            val categories = categoryService.getCategoriesByUserId(currentUserId)
            val predefinedCategories = categoryService.getPredefinedCategories()
            val allCategories = (predefinedCategories + categories).distinctBy { it.categoryName }

            val categoriesWithTotals = allCategories.map { category ->
                val totalSpentInCategory = expenses
                    .filter { it.category.equals(category.categoryName, ignoreCase = true) }
                    .sumOf { it.amount }
                Triple(category.categoryName, totalSpentInCategory, category.categoryId < 0)
            }.filter { it.second > 0 || it.third } // Show categories with spending OR predefined categories
                .sortedByDescending { it.second }

            runOnUiThread {
                if (categoriesWithTotals.isEmpty()) {
                    lvCategories.visibility = android.view.View.GONE
                    emptyText.visibility = android.view.View.VISIBLE
                    if (startDateFilter != null) {
                        emptyText.text = "No expenses found for selected dates.\nTap 'Create Category' to add one."
                    } else {
                        emptyText.text = "No categories yet.\nTap 'Create Category' to add one."
                    }
                } else {
                    lvCategories.visibility = android.view.View.VISIBLE
                    emptyText.visibility = android.view.View.GONE

                    val categoryStrings = categoriesWithTotals.map { (categoryName, spent, isPredefined) ->
                        val tag = if (isPredefined) "📌" else "➕"
                        val percentage = if (totalSpent > 0) {
                            (spent / totalSpent * 100).toInt()
                        } else {
                            0
                        }
                        "$tag $categoryName\n   Spent: R${String.format(Locale.US, "%.2f", spent)}  ($percentage% of total)"
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        categoryStrings
                    )
                    lvCategories.adapter = adapter

                    lvCategories.setOnItemClickListener { _, _, position, _ ->
                        val (categoryName, spent, isPredefined) = categoriesWithTotals[position]
                        val type = if (isPredefined) "Predefined" else "Custom"
                        Toast.makeText(
                            this,
                            "$type Category: $categoryName\nTotal Spent: R${String.format(Locale.US, "%.2f", spent)}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "Error loading categories: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}