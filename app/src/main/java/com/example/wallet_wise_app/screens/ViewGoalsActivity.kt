// screens/ViewGoalsActivity.kt
package com.example.wallet_wise_app.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.services.GoalService
import java.util.Locale

class ViewGoalsActivity : AppCompatActivity() {

    private lateinit var tvCurrentSpending: TextView
    private lateinit var tvMinimumGoal: TextView
    private lateinit var tvMaximumGoal: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressText: TextView
    private lateinit var tvStatusMessage: TextView
    private lateinit var btnSetNewGoal: Button

    private lateinit var goalService: GoalService
    private lateinit var dbHelper: DatabaseHelper

    // For now, using fixed user ID (same as SetGoalsActivity)
    private val currentUserId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_goals)

        // Initialize Services
        goalService = GoalService(this)
        dbHelper = DatabaseHelper(this)

        // Initialize Views
        tvCurrentSpending = findViewById(R.id.tvCurrentSpending)
        tvMinimumGoal = findViewById(R.id.tvMinimumGoal)
        tvMaximumGoal = findViewById(R.id.tvMaximumGoal)
        progressBar = findViewById(R.id.progressBar)
        tvProgressText = findViewById(R.id.tvProgressText)
        tvStatusMessage = findViewById(R.id.tvStatusMessage)
        btnSetNewGoal = findViewById(R.id.btnSetNewGoal)

        // Set New Goal Button
        btnSetNewGoal.setOnClickListener {
            val intent = Intent(this, SetGoalsActivity::class.java)
            startActivity(intent)
        }

        // Load data
        loadGoalsAndProgress()
    }

    override fun onResume() {
        super.onResume()
        // Refresh when coming back from setting new goal
        loadGoalsAndProgress()
    }

    private fun loadGoalsAndProgress() {
        Thread {
            try {
                // Get user's goals
                val goals = goalService.getGoalsByUserId(currentUserId)
                // Get current total spending from expenses
                val expenses = dbHelper.getAllExpenses()
                val currentSpending = expenses.sumOf { it.amount }

                runOnUiThread {
                    // Display current spending
                    tvCurrentSpending.text = String.format(Locale.US, "R%.2f", currentSpending)

                    if (goals.isEmpty()) {
                        // No goals set
                        tvMinimumGoal.text = "R0"
                        tvMaximumGoal.text = "R0"
                        progressBar.progress = 0
                        tvProgressText.text = "0% of maximum goal"
                        tvStatusMessage.text = "No goals set yet. Tap 'Set New Goal' to create one."
                        tvStatusMessage.setBackgroundColor(resources.getColor(android.R.color.holo_orange_light))
                    } else {
                        // Display first goal (simplified)
                        val goal = goals.first()
                        val minimum = goal.minimumGoal
                        val maximum = goal.maximumGoal

                        tvMinimumGoal.text = String.format(Locale.US, "R%d", minimum)
                        tvMaximumGoal.text = String.format(Locale.US, "R%d", maximum)

                        // Calculate progress percentage (toward maximum goal)
                        var progressPercent = (currentSpending / maximum * 100).toInt()
                        if (progressPercent > 100) progressPercent = 100
                        progressBar.progress = progressPercent
                        tvProgressText.text = "$progressPercent% of maximum goal (R$maximum)"

                        // Determine status message
                        val statusMessage = when {
                            currentSpending < minimum -> {
                                val remaining = minimum - currentSpending
                                tvStatusMessage.setBackgroundColor(resources.getColor(android.R.color.holo_orange_light))
                                "⚠️ Below minimum goal! Spend R${String.format("%.2f", remaining)} more to reach your minimum."
                            }
                            currentSpending > maximum -> {
                                val over = currentSpending - maximum
                                tvStatusMessage.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                                "❌ Over maximum goal! You've exceeded by R${String.format("%.2f", over)}."
                            }
                            else -> {
                                val within = maximum - currentSpending
                                tvStatusMessage.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                                "✅ On track! You have R${String.format("%.2f", within)} left until your maximum goal."
                            }
                        }
                        tvStatusMessage.text = statusMessage
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error loading goals: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}