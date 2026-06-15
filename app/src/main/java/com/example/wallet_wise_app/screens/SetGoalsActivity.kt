// screens/SetGoalsActivity.kt
package com.example.wallet_wise_app.screens

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.services.GoalService

class SetGoalsActivity : AppCompatActivity() {

    private lateinit var etMinimumGoal: EditText
    private lateinit var etMaximumGoal: EditText
    private lateinit var btnSetGoals: Button
    private lateinit var btnViewGoals: Button
    private lateinit var btnMenu: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navExpenseList: Button
    private lateinit var navViewGoals: Button
    private lateinit var navSetGoals: Button
    private lateinit var navCreateCategory: Button
    private lateinit var navViewCategories: Button
    private lateinit var navProjectionCalendar: Button
    private lateinit var goalService: GoalService

    private val currentUserId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_goals)

        goalService = GoalService(this)

        etMinimumGoal = findViewById(R.id.etMinimumGoal)
        etMaximumGoal = findViewById(R.id.etMaximumGoal)
        btnSetGoals = findViewById(R.id.btnSetGoals)
        btnViewGoals = findViewById(R.id.btnViewGoals)
        btnMenu = findViewById(R.id.btnMenu)
        drawerLayout = findViewById(R.id.drawerLayout)
        navExpenseList = findViewById(R.id.navExpenseList)
        navViewGoals = findViewById(R.id.navViewGoals)
        navSetGoals = findViewById(R.id.navSetGoals)
        navCreateCategory = findViewById(R.id.navCreateCategory)
        navViewCategories = findViewById(R.id.navViewCategories)
        navProjectionCalendar = findViewById(R.id.navProjectionCalendar)

        setupDrawer()

        btnSetGoals.setOnClickListener {
            saveGoal()
        }

        btnViewGoals.setOnClickListener {
            startActivity(Intent(this, ViewGoalsActivity::class.java))
        }
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
            startActivity(Intent(this, ProjectionCalendarActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun saveGoal() {
        val minimumText = etMinimumGoal.text.toString()
        val maximumText = etMaximumGoal.text.toString()

        if (minimumText.isBlank()) {
            Toast.makeText(this, "Please enter minimum goal", Toast.LENGTH_SHORT).show()
            return
        }

        if (maximumText.isBlank()) {
            Toast.makeText(this, "Please enter maximum goal", Toast.LENGTH_SHORT).show()
            return
        }

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

        btnSetGoals.isEnabled = false
        btnSetGoals.text = "Saving..."

        Thread {
            try {
                val savedGoal = goalService.addGoal(
                    minimumGoal = minimumGoal,
                    maximumGoal = maximumGoal,
                    userId = currentUserId
                )

                runOnUiThread {
                    Toast.makeText(this, "Goal saved! Min: R$minimumGoal, Max: R$maximumGoal", Toast.LENGTH_LONG).show()
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