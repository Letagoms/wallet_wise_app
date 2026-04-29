package com.example.wallet_wise_app

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.databinding.ActivityExpenseListBinding
import java.io.File
import java.util.Locale

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private lateinit var dbHelper: ExpenseDatabaseHelper
    private var userId: Int = -1

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

       /* binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(binding.navigationView)
            true
        }***/
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {
        val expenses = dbHelper.getAllExpenses(userId)
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