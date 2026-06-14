// screens/ViewGoalsActivity.kt
package com.example.wallet_wise_app.screens

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.services.GoalService
import java.util.Locale

class ViewGoalsActivity : AppCompatActivity() {

    private lateinit var tvCurrentSpending: TextView
    private lateinit var tvMinimumGoal: TextView
    private lateinit var tvMaximumGoal: TextView
    private lateinit var progressBarMinimum: ProgressBar
    private lateinit var progressBarMaximum: ProgressBar
    private lateinit var tvProgressText: TextView
    private lateinit var tvStatusMessage: TextView
    private lateinit var btnSetNewGoal: Button
    private lateinit var btnMenu: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navExpenseList: Button
    private lateinit var navViewGoals: Button
    private lateinit var navSetGoals: Button

    private lateinit var goalService: GoalService
    private lateinit var dbHelper: DatabaseHelper

    private val currentUserId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_goals)

        goalService = GoalService(this)
        dbHelper = DatabaseHelper(this)

        tvCurrentSpending = findViewById(R.id.tvCurrentSpending)
        tvMinimumGoal = findViewById(R.id.tvMinimumGoal)
        tvMaximumGoal = findViewById(R.id.tvMaximumGoal)
        progressBarMinimum = findViewById(R.id.progressBarMinimum)
        progressBarMaximum = findViewById(R.id.progressBarMaximum)
        tvProgressText = findViewById(R.id.tvProgressText)
        tvStatusMessage = findViewById(R.id.tvStatusMessage)
        btnSetNewGoal = findViewById(R.id.btnSetNewGoal)
        btnMenu = findViewById(R.id.btnMenu)
        drawerLayout = findViewById(R.id.drawerLayout)
        navExpenseList = findViewById(R.id.navExpenseList)
        navViewGoals = findViewById(R.id.navViewGoals)
        navSetGoals = findViewById(R.id.navSetGoals)

        // Menu button opens drawer
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        // Navigation buttons
        navExpenseList.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
            drawerLayout.closeDrawer(Gravity.START)
        }

        navViewGoals.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.START)
        }

        navSetGoals.setOnClickListener {
            startActivity(Intent(this, SetGoalsActivity::class.java))
            drawerLayout.closeDrawer(Gravity.START)
        }

        btnSetNewGoal.setOnClickListener {
            startActivity(Intent(this, SetGoalsActivity::class.java))
        }

        loadGoalsAndProgress()
    }

    override fun onResume() {
        super.onResume()
        loadGoalsAndProgress()
    }

    private fun loadGoalsAndProgress() {
        Thread {
            try {
                val goals = goalService.getGoalsByUserId(currentUserId)
                val expenses = dbHelper.getAllExpenses()
                val currentSpending = expenses.sumOf { it.amount }

                runOnUiThread {
                    tvCurrentSpending.text = String.format(Locale.US, "R%.2f", currentSpending)

                    if (goals.isEmpty()) {
                        tvMinimumGoal.text = "R0"
                        tvMaximumGoal.text = "R0"
                        progressBarMinimum.progress = 0
                        progressBarMaximum.progress = 0
                        tvProgressText.text = "No goals set"
                        tvStatusMessage.text = "No goals set yet. Tap 'Set New Goal' to create one."
                    } else {
                        val goal = goals.first()
                        val minimum = goal.minimumGoal
                        val maximum = goal.maximumGoal

                        tvMinimumGoal.text = String.format(Locale.US, "R%d", minimum)
                        tvMaximumGoal.text = String.format(Locale.US, "R%d", maximum)

                        // Calculate progress for minimum goal (0% if below, 100% if reached or exceeded)
                        var minProgress = 0
                        if (currentSpending >= minimum) {
                            minProgress = 100
                        } else {
                            minProgress = (currentSpending / minimum * 100).toInt()
                        }
                        progressBarMinimum.progress = minProgress

                        // Calculate progress for maximum goal (capped at 100%)
                        var maxProgress = (currentSpending / maximum * 100).toInt()
                        if (maxProgress > 100) maxProgress = 100
                        progressBarMaximum.progress = maxProgress

                        // Progress text
                        tvProgressText.text = when {
                            currentSpending < minimum -> "📍 Need R${String.format("%.2f", minimum - currentSpending)} more to reach minimum goal"
                            currentSpending > maximum -> "⚠️ Exceeded maximum by R${String.format("%.2f", currentSpending - maximum)}"
                            else -> "🎯 Between minimum and maximum goals!"
                        }

                        // Status message
                        val statusMessage = when {
                            currentSpending < minimum -> "⚠️ Below minimum goal! Spend R${String.format("%.2f", minimum - currentSpending)} more to reach R$minimum"
                            currentSpending > maximum -> "❌ Over maximum goal! You've exceeded by R${String.format("%.2f", currentSpending - maximum)}"
                            else -> "✅ On track! Between R$minimum and R$maximum"
                        }
                        tvStatusMessage.text = statusMessage
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}