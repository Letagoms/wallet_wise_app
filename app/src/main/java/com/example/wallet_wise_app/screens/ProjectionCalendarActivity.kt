// screens/ProjectionCalendarActivity.kt
package com.example.wallet_wise_app.screens

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.model.ProjectedExpense
import com.example.wallet_wise_app.model.ProjectedIncome
import java.text.SimpleDateFormat
import java.util.*

class ProjectionCalendarActivity : AppCompatActivity() {

    private lateinit var btnMenu: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var tvMonthYear: TextView
    private lateinit var btnPrevMonth: Button
    private lateinit var btnNextMonth: Button
    private lateinit var tvProjectedBalance: TextView
    private lateinit var tvExpectedSavings: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var btnAddExpense: Button
    private lateinit var btnAddIncome: Button
    private lateinit var lvTransactions: ListView
    private lateinit var tvTransactionCount: TextView

    private lateinit var navExpenseList: Button
    private lateinit var navViewGoals: Button
    private lateinit var navSetGoals: Button
    private lateinit var navCreateCategory: Button
    private lateinit var navViewCategories: Button
    private lateinit var navProjectionCalendar: Button
    private lateinit var navGamification: Button

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId = 1
    private var currentCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projection_calendar)

        dbHelper = DatabaseHelper.getInstance(this)

        currentUserId = intent.getIntExtra("USER_ID", 1)

        initViews()
        setupDrawer()
        setupCalendar()
        loadData()

        btnAddExpense.setOnClickListener {
            showAddExpenseDialog()
        }

        btnAddIncome.setOnClickListener {
            showAddIncomeDialog()
        }
    }

    private fun initViews() {
        btnMenu = findViewById(R.id.btnMenu)
        drawerLayout = findViewById(R.id.drawerLayout)
        tvMonthYear = findViewById(R.id.tvMonthYear)
        btnPrevMonth = findViewById(R.id.btnPrevMonth)
        btnNextMonth = findViewById(R.id.btnNextMonth)
        tvProjectedBalance = findViewById(R.id.tvProjectedBalance)
        tvExpectedSavings = findViewById(R.id.tvExpectedSavings)
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnAddIncome = findViewById(R.id.btnAddIncome)
        lvTransactions = findViewById(R.id.lvTransactions)
        tvTransactionCount = findViewById(R.id.tvTransactionCount)

        navExpenseList = findViewById(R.id.navExpenseList)
        navViewGoals = findViewById(R.id.navViewGoals)
        navSetGoals = findViewById(R.id.navSetGoals)
        navCreateCategory = findViewById(R.id.navCreateCategory)
        navViewCategories = findViewById(R.id.navViewCategories)
        navProjectionCalendar = findViewById(R.id.navProjectionCalendar)
        navGamification = findViewById(R.id.navGamification)
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
            startActivity(Intent(this, ViewCategoriesActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navProjectionCalendar.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navGamification.setOnClickListener {
            startActivity(Intent(this, GamificationActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun setupCalendar() {
        updateMonthDisplay()

        btnPrevMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateMonthDisplay()
            loadData()
        }

        btnNextMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateMonthDisplay()
            loadData()
        }
    }

    private fun updateMonthDisplay() {
        val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthYear.text = format.format(currentCalendar.time)
    }

    private fun loadData() {
        Thread {
            val year = currentCalendar.get(Calendar.YEAR)
            val month = currentCalendar.get(Calendar.MONTH) + 1

            val expenses = dbHelper.getProjectedExpensesByMonth(currentUserId, year, month)
            val incomes = dbHelper.getProjectedIncomesByMonth(currentUserId, year, month)

            val totalExpenses = expenses.sumOf { it.amount }
            val totalIncome = incomes.sumOf { it.amount }
            val projectedBalance = totalIncome - totalExpenses
            val expectedSavings = projectedBalance

            val transactions = mutableListOf<Pair<String, String>>()
            expenses.forEach {
                transactions.add(Pair("EXPENSE", "${it.date} - ${it.name}: -R${String.format("%.2f", it.amount)}"))
            }
            incomes.forEach {
                transactions.add(Pair("INCOME", "${it.date} - ${it.name}: +R${String.format("%.2f", it.amount)}"))
            }
            transactions.sortBy { it.second }

            runOnUiThread {
                tvTotalExpenses.text = String.format("R%.2f", totalExpenses)
                tvTotalIncome.text = String.format("R%.2f", totalIncome)
                tvProjectedBalance.text = String.format("R%.2f", projectedBalance)
                tvExpectedSavings.text = String.format("R%.2f", expectedSavings)
                tvTransactionCount.text = "${transactions.size} Transactions"

                val transactionStrings = transactions.map { it.second }
                val adapter = ArrayAdapter(this@ProjectionCalendarActivity, android.R.layout.simple_list_item_1, transactionStrings)
                lvTransactions.adapter = adapter
            }
        }.start()
    }

    private fun showAddExpenseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_projected_transaction, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val defaultDate = dateFormat.format(currentCalendar.time)
        etDate.setText(defaultDate)
        etDate.hint = "YYYY-MM-DD"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Projected Expense")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val date = etDate.text.toString()

                if (name.isNotBlank() && amount > 0 && date.isNotBlank()) {
                    Thread {
                        val expense = ProjectedExpense(
                            name = name,
                            amount = amount,
                            date = date,
                            userId = currentUserId
                        )
                        dbHelper.insertProjectedExpense(expense)
                        runOnUiThread { loadData() }
                    }.start()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun showAddIncomeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_projected_transaction, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val defaultDate = dateFormat.format(currentCalendar.time)
        etDate.setText(defaultDate)
        etDate.hint = "YYYY-MM-DD"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Projected Income")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val date = etDate.text.toString()

                if (name.isNotBlank() && amount > 0 && date.isNotBlank()) {
                    Thread {
                        val income = ProjectedIncome(
                            name = name,
                            amount = amount,
                            date = date,
                            userId = currentUserId
                        )
                        dbHelper.insertProjectedIncome(income)
                        runOnUiThread { loadData() }
                    }.start()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
}