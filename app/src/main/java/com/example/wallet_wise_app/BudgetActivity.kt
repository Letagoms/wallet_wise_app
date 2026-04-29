package com.example.wallet_wise_app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BudgetActivity : AppCompatActivity() {

    private lateinit var categoryManager: CategoryManager
    private lateinit var expenseManager: ExpenseManager
    private lateinit var adapter: CategoryAdapter
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        categoryManager = CategoryManager(this)
        expenseManager = ExpenseManager(this)
        userId = intent.getIntExtra("USER_ID", -1)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CategoryAdapter(emptyList())
        recyclerView.adapter = adapter

        findViewById<android.widget.ImageButton>(R.id.btnMenu).setOnClickListener {
            finish()
        }

        findViewById<android.widget.Button>(R.id.btnCreateCategory).setOnClickListener {
            val intent = Intent(this, CreateCategoryActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val categories = categoryManager.getCategories(userId)

        // This month's date range
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val spentMap = expenseManager.getSpendingByCategory(startDate, endDate, userId)

        val totalSpent = spentMap.values.sum()
        val totalGoal = categories.sumOf { it.maxGoal }

        findViewById<TextView>(R.id.tvTotalSpent).text = "R%.2f".format(totalSpent)
        findViewById<TextView>(R.id.tvBudgetGoal).text = "R%.2f".format(totalGoal)

        adapter.updateData(categories)
        adapter.setSpentData(spentMap)
    }
}