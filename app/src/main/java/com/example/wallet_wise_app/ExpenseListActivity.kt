package com.example.wallet_wise_app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.databinding.ActivityExpenseListBinding

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

    private fun loadExpenses() {
        val expenses = dbHelper.getAllExpenses()
        val displayList = mutableListOf<String>()

        for (expense in expenses) {
            displayList.add("${expense.date} - ${expense.description}\n${expense.category} • ${expense.time}\n-R${String.format("%.2f", expense.amount)}")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
        binding.lvExpenses.adapter = adapter
    }
}