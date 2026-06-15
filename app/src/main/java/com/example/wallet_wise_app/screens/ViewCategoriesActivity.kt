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
import com.example.wallet_wise_app.services.CategoryService
import java.util.Locale

class ViewCategoriesActivity : AppCompatActivity() {

    private lateinit var lvCategories: ListView
    private lateinit var emptyText: TextView
    private lateinit var btnCreateCategory: Button
    private lateinit var btnMenu: ImageButton
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_categories)

        categoryService = CategoryService(this)
        dbHelper = DatabaseHelper.getInstance(this)

        lvCategories = findViewById(R.id.lvCategories)
        emptyText = findViewById(R.id.emptyText)
        btnCreateCategory = findViewById(R.id.btnCreateCategory)
        btnMenu = findViewById(R.id.btnMenu)
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

        loadCategories()
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

    override fun onResume() {
        super.onResume()
        loadCategories()
    }

    private fun loadCategories() {
        Thread {
            try {
                val categoriesWithTotals = categoryService.getCategoriesWithTotals(currentUserId)
                val allExpenses = dbHelper.getAllExpenses()
                val totalSpentAll = allExpenses.sumOf { it.amount }

                runOnUiThread {
                    if (categoriesWithTotals.isEmpty()) {
                        lvCategories.visibility = android.view.View.GONE
                        emptyText.visibility = android.view.View.VISIBLE
                    } else {
                        lvCategories.visibility = android.view.View.VISIBLE
                        emptyText.visibility = android.view.View.GONE

                        val categoryStrings = categoriesWithTotals.map { categoryWithTotal ->
                            val categoryName = categoryWithTotal.category.categoryName
                            val spent = categoryWithTotal.totalSpent
                            val percentage = if (totalSpentAll > 0) {
                                (spent / totalSpentAll * 100).toInt()
                            } else {
                                0
                            }
                            val isPredefined = categoryWithTotal.isPredefined
                            val tag = if (isPredefined) "📌" else "➕"

                            "$tag $categoryName\n   Spent: R${String.format(Locale.US, "%.2f", spent)}  ($percentage% of total)"
                        }

                        val adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1,
                            categoryStrings
                        )
                        lvCategories.adapter = adapter

                        lvCategories.setOnItemClickListener { _, _, position, _ ->
                            val category = categoriesWithTotals[position].category
                            val spent = categoriesWithTotals[position].totalSpent
                            val type = if (categoriesWithTotals[position].isPredefined) "Predefined" else "Custom"
                            Toast.makeText(
                                this,
                                "$type Category: ${category.categoryName}\nTotal Spent: R${String.format(Locale.US, "%.2f", spent)}",
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
        }.start()
    }
}