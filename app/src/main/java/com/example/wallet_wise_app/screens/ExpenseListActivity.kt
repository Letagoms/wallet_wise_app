// screens/ExpenseListActivity.kt
package com.example.wallet_wise_app.screens

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.models.Expense
import java.io.File
import java.util.Locale

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var lvExpenses: ListView
    private lateinit var emptyText: TextView
    private lateinit var btnAddExpense: Button
    private lateinit var btnMenu: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navExpenseList: Button
    private lateinit var navViewGoals: Button
    private lateinit var navSetGoals: Button
    private lateinit var dbHelper: DatabaseHelper

    private var expensesList: List<Expense> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        lvExpenses = findViewById(R.id.lvExpenses)
        emptyText = findViewById(R.id.emptyText)
        btnAddExpense = findViewById(R.id.btnGoToAddExpense)
        btnMenu = findViewById(R.id.btnMenu)
        drawerLayout = findViewById(R.id.drawerLayout)
        navExpenseList = findViewById(R.id.navExpenseList)
        navViewGoals = findViewById(R.id.navViewGoals)
        navSetGoals = findViewById(R.id.navSetGoals)
        dbHelper = DatabaseHelper(this)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        navExpenseList.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.START)
        }

        navViewGoals.setOnClickListener {
            startActivity(Intent(this, ViewGoalsActivity::class.java))
            drawerLayout.closeDrawer(Gravity.START)
        }

        navSetGoals.setOnClickListener {
            startActivity(Intent(this, SetGoalsActivity::class.java))
            drawerLayout.closeDrawer(Gravity.START)
        }

        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, CreateExpenseActivity::class.java))
        }

        loadExpenses()
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {
        Thread {
            expensesList = dbHelper.getAllExpenses()

            runOnUiThread {
                if (expensesList.isEmpty()) {
                    lvExpenses.visibility = android.view.View.GONE
                    emptyText.visibility = android.view.View.VISIBLE
                } else {
                    lvExpenses.visibility = android.view.View.VISIBLE
                    emptyText.visibility = android.view.View.GONE

                    val expenseStrings = expensesList.map {
                        val hasPhoto = if (it.photo.isNotBlank()) "📷" else ""
                        "${hasPhoto} ${it.name} - R${String.format(Locale.US, "%.2f", it.amount)}\n" +
                                "📁 ${it.category} | 📅 ${it.date} | 🕐 ${it.startTime} - ${it.endTime}\n" +
                                "📝 ${it.description}"
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        expenseStrings
                    )
                    lvExpenses.adapter = adapter

                    // Handle item click to show image
                    lvExpenses.setOnItemClickListener { _, _, position, _ ->
                        val expense = expensesList[position]
                        if (expense.photo.isNotBlank()) {
                            showImagePreview(expense.photo, expense.name)
                        } else {
                            Toast.makeText(this, "No photo attached to this expense", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }.start()
    }

    private fun showImagePreview(photoPath: String, expenseName: String) {
        // Check if file exists
        val imageFile = File(photoPath)
        if (!imageFile.exists()) {
            Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Create dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_preview, null)
        val ivFullImage = dialogView.findViewById<ImageView>(R.id.ivFullImage)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)

        // Load image
        val bitmap = BitmapFactory.decodeFile(photoPath)
        ivFullImage.setImageBitmap(bitmap)

        // Build and show dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}