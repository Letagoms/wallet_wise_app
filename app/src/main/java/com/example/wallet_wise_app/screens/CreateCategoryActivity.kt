// screens/CreateCategoryActivity.kt
package com.example.wallet_wise_app.screens

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.services.CategoryService

class CreateCategoryActivity : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var btnCreateCategory: Button
    private lateinit var btnViewCategories: Button
    private lateinit var btnMenu: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navExpenseList: Button
    private lateinit var navViewGoals: Button
    private lateinit var navSetGoals: Button
    private lateinit var navCreateCategory: Button
    private lateinit var navViewCategories: Button
    private lateinit var categoryService: CategoryService

    // For now, using fixed user ID
    private val currentUserId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        categoryService = CategoryService(this)

        // Initialize views
        etCategoryName = findViewById(R.id.etCategoryName)
        btnCreateCategory = findViewById(R.id.btnCreateCategory)
        btnViewCategories = findViewById(R.id.btnViewCategories)
        btnMenu = findViewById(R.id.btnMenu)
        drawerLayout = findViewById(R.id.drawerLayout)
        navExpenseList = findViewById(R.id.navExpenseList)
        navViewGoals = findViewById(R.id.navViewGoals)
        navSetGoals = findViewById(R.id.navSetGoals)
        navCreateCategory = findViewById(R.id.navCreateCategory)
        navViewCategories = findViewById(R.id.navViewCategories)

        // Setup navigation drawer
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
            drawerLayout.closeDrawer(GravityCompat.START)
            // Already on Create Category screen
        }

        navViewCategories.setOnClickListener {
            startActivity(Intent(this, ViewCategoriesActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Create Category button
        btnCreateCategory.setOnClickListener {
            createCategory()
        }

        // View Categories button
        btnViewCategories.setOnClickListener {
            startActivity(Intent(this, ViewCategoriesActivity::class.java))
        }
    }

    private fun createCategory() {
        val categoryName = etCategoryName.text.toString().trim()

        // Validation
        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable button while saving
        btnCreateCategory.isEnabled = false
        btnCreateCategory.text = "Creating..."

        Thread {
            try {
                val savedCategory = categoryService.addCategory(
                    categoryName = categoryName,
                    userId = currentUserId
                )

                runOnUiThread {
                    Toast.makeText(this, "Category '${savedCategory.categoryName}' created!", Toast.LENGTH_LONG).show()
                    etCategoryName.text.clear()
                    btnCreateCategory.isEnabled = true
                    btnCreateCategory.text = "Create Category"
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    btnCreateCategory.isEnabled = true
                    btnCreateCategory.text = "Create Category"
                }
            }
        }.start()
    }
}