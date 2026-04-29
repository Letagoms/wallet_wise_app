package com.example.wallet_wise_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class Budget : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        dbHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CategoryAdapter(emptyList())
        recyclerView.adapter = adapter

        loadCategories()

        val createButton: Button = findViewById(R.id.btnCreateCategory)
        createButton.setOnClickListener {
            val intent = Intent(this, CreateCategory::class.java)
            startActivity(intent)
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.nav_budget
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, ExpenseListActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_budget -> true
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
        findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.nav_budget
    }

    private fun loadCategories() {
        val categories = dbHelper.getAllCategories()
        val spentData: Map<Int, Double> = categories.associate { it.id to 0.0 }

        val totalSpent = spentData.values.sum()
        val totalGoal  = categories.sumOf { it.maxGoal }

        findViewById<TextView>(R.id.tvTotalSpent).text = "R%.2f".format(totalSpent)
        findViewById<TextView>(R.id.tvBudgetGoal).text = "R%.2f".format(totalGoal)

        adapter.updateData(categories)
        adapter.setSpentData(spentData)
    }
}