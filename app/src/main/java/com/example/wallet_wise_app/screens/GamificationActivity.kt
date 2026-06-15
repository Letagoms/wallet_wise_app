// screens/GamificationActivity.kt
package com.example.wallet_wise_app.screens

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.services.GamificationService

class GamificationActivity : AppCompatActivity() {

    private lateinit var btnMenu: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var tvStatsSummary: TextView
    private lateinit var lvUnlockedAchievements: ListView
    private lateinit var lvLockedAchievements: ListView

    private lateinit var navExpenseList: Button
    private lateinit var navViewGoals: Button
    private lateinit var navSetGoals: Button
    private lateinit var navCreateCategory: Button
    private lateinit var navViewCategories: Button
    private lateinit var navProjectionCalendar: Button
    private lateinit var navGamification: Button

    private lateinit var gamificationService: GamificationService
    private lateinit var dbHelper: DatabaseHelper

    private val currentUserId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamification)

        gamificationService = GamificationService(this)
        dbHelper = DatabaseHelper.getInstance(this)

        initViews()
        setupDrawer()
        loadAchievements()
        loadStats()
    }

    private fun initViews() {
        btnMenu = findViewById(R.id.btnMenu)
        drawerLayout = findViewById(R.id.drawerLayout)
        tvStatsSummary = findViewById(R.id.tvStatsSummary)
        lvUnlockedAchievements = findViewById(R.id.lvUnlockedAchievements)
        lvLockedAchievements = findViewById(R.id.lvLockedAchievements)

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
            startActivity(Intent(this, ProjectionCalendarActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navGamification.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun loadStats() {
        val stats = dbHelper.getUserStats(currentUserId)
        if (stats != null) {
            tvStatsSummary.text = "📊 Your Progress: ${stats.loginCount} Logins | ${stats.expenseCount} Expenses | ${stats.goalCount} Goals"
        } else {
            tvStatsSummary.text = "📊 Your Progress: 0 Logins | 0 Expenses | 0 Goals"
        }
    }

    private fun loadAchievements() {
        val achievements = gamificationService.getUserAchievements(currentUserId)

        val unlocked = achievements.filter { it.isUnlocked }
        val locked = achievements.filter { !it.isUnlocked }

        val unlockedStrings = unlocked.map {
            "${it.icon} ${it.name}\n   ✅ ${it.description} - Unlocked on ${it.unlockedAt}"
        }

        val lockedStrings = locked.map {
            "${it.icon} ${it.name}\n   🔒 ${it.description} (Need ${it.requiredValue})"
        }

        val unlockedAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, unlockedStrings)
        val lockedAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lockedStrings)

        lvUnlockedAchievements.adapter = unlockedAdapter
        lvLockedAchievements.adapter = lockedAdapter
    }

    override fun onResume() {
        super.onResume()
        loadAchievements()
        loadStats()
    }
}