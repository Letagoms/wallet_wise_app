// screens/SetGoalsActivity.kt
package com.example.wallet_wise_app.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.services.GoalService

class SetGoalsActivity : AppCompatActivity() {

    private lateinit var etMinimumGoal: EditText
    private lateinit var etMaximumGoal: EditText
    private lateinit var btnSetGoals: Button
    private lateinit var btnViewGoals: Button
    private lateinit var goalService: GoalService

    // For now, using fixed user ID (you can change this later when you add login)
    private val currentUserId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_goals)

        // Initialize Service
        goalService = GoalService(this)

        // Initialize Views
        etMinimumGoal = findViewById(R.id.etMinimumGoal)
        etMaximumGoal = findViewById(R.id.etMaximumGoal)
        btnSetGoals = findViewById(R.id.btnSetGoals)
        btnViewGoals = findViewById(R.id.btnViewGoals)

        // Set Goals Button Click
        btnSetGoals.setOnClickListener {
            saveGoal()
        }

        // View Goals Button Click
        btnViewGoals.setOnClickListener {
            val intent = Intent(this, ViewGoalsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveGoal() {
        // Get values from input fields
        val minimumText = etMinimumGoal.text.toString()
        val maximumText = etMaximumGoal.text.toString()

        // Validate inputs are not empty
        if (minimumText.isBlank()) {
            Toast.makeText(this, "Please enter minimum goal", Toast.LENGTH_SHORT).show()
            return
        }

        if (maximumText.isBlank()) {
            Toast.makeText(this, "Please enter maximum goal", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert to numbers
        val minimumGoal = minimumText.toIntOrNull()
        val maximumGoal = maximumText.toIntOrNull()

        if (minimumGoal == null) {
            Toast.makeText(this, "Invalid minimum goal amount", Toast.LENGTH_SHORT).show()
            return
        }

        if (maximumGoal == null) {
            Toast.makeText(this, "Invalid maximum goal amount", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable button while saving
        btnSetGoals.isEnabled = false
        btnSetGoals.text = "Saving..."

        // Save in background thread
        Thread {
            try {
                val savedGoal = goalService.addGoal(
                    minimumGoal = minimumGoal,
                    maximumGoal = maximumGoal,
                    userId = currentUserId
                )

                runOnUiThread {
                    Toast.makeText(this, "Goal saved! Min: R$minimumGoal, Max: R$maximumGoal", Toast.LENGTH_LONG).show()

                    // Clear fields after successful save
                    etMinimumGoal.text.clear()
                    etMaximumGoal.text.clear()

                    btnSetGoals.isEnabled = true
                    btnSetGoals.text = "Set Goals"
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    btnSetGoals.isEnabled = true
                    btnSetGoals.text = "Set Goals"
                }
            }
        }.start()
    }
}