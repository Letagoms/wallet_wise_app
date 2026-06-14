// services/CategoryService.kt
package com.example.wallet_wise_app.services

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.model.Category
import com.example.wallet_wise_app.model.Expense
import com.example.wallet_wise_app.repository.CategoryRepository

class CategoryService(private val context: Context) {

    private val repository = CategoryRepository(context)
    private val dbHelper = DatabaseHelper.getInstance(context)

    // Predefined default categories
    fun getPredefinedCategories(): List<Category> {
        return listOf(
            Category(categoryId = -1, categoryName = "Food", userId = 0),
            Category(categoryId = -2, categoryName = "Transport", userId = 0),
            Category(categoryId = -3, categoryName = "Shopping", userId = 0),
            Category(categoryId = -4, categoryName = "Bills", userId = 0),
            Category(categoryId = -5, categoryName = "Entertainment", userId = 0),
            Category(categoryId = -6, categoryName = "Healthcare", userId = 0),
            Category(categoryId = -7, categoryName = "Rent", userId = 0),
            Category(categoryId = -8, categoryName = "Groceries", userId = 0),
            Category(categoryId = -9, categoryName = "Clothing", userId = 0),
            Category(categoryId = -10, categoryName = "Education", userId = 0)
        )
    }

    // Get ALL categories (predefined + user-created)
    fun getAllCategoriesCombined(userId: Int): List<Category> {
        val predefined = getPredefinedCategories()
        val userCreated = repository.getCategoriesByUserId(userId)
        return predefined + userCreated
    }

    fun addCategory(
        categoryName: String,
        userId: Int
    ): Category {
        if (categoryName.isBlank()) {
            throw Exception("Category name is required")
        }

        if (categoryName.length < 2) {
            throw Exception("Category name must be at least 2 characters")
        }

        if (categoryName.length > 120) {
            throw Exception("Category name cannot exceed 120 characters")
        }

        // Check against both predefined and user-created categories
        val allCategoryNames = getAllCategoriesCombined(userId).map { it.categoryName }
        if (allCategoryNames.any { it.equals(categoryName, ignoreCase = true) }) {
            throw Exception("Category '$categoryName' already exists")
        }

        if (userId <= 0) {
            throw Exception("Valid user ID is required")
        }

        val category = Category(
            categoryId = 0,
            categoryName = categoryName.trim(),
            userId = userId
        )

        return repository.save(category)
    }

    fun getAllCategories(): List<Category> {
        return repository.getAllCategories()
    }

    fun getCategoriesByUserId(userId: Int): List<Category> {
        return repository.getCategoriesByUserId(userId)
    }

    fun getCategoryNames(userId: Int): List<String> {
        val allCategories = getAllCategoriesCombined(userId)
        return allCategories.map { it.categoryName }.distinct()
    }

    fun doesCategoryExist(categoryName: String, userId: Int): Boolean {
        val allCategoryNames = getAllCategoriesCombined(userId).map { it.categoryName }
        return allCategoryNames.any { it.equals(categoryName, ignoreCase = true) }
    }

    fun deleteCategory(categoryId: Int): Boolean {
        // Only allow deletion of user-created categories (predefined have negative IDs)
        if (categoryId < 0) {
            throw Exception("Cannot delete predefined category")
        }
        return repository.deleteCategory(categoryId)
    }

    fun getTotalSpentByCategory(categoryName: String, userId: Int, expenses: List<Expense>): Double {
        return expenses
            .filter { it.category.equals(categoryName, ignoreCase = true) }
            .sumOf { it.amount }
    }

    data class CategoryWithTotal(
        val category: Category,
        val totalSpent: Double,
        val isPredefined: Boolean
    )

    fun getCategoriesWithTotals(userId: Int): List<CategoryWithTotal> {
        val allCategories = getAllCategoriesCombined(userId)
        val expenses = dbHelper.getAllExpenses()
        val totalSpentAll = expenses.sumOf { it.amount }

        return allCategories.map { category ->
            val totalSpent = getTotalSpentByCategory(category.categoryName, userId, expenses)
            CategoryWithTotal(category, totalSpent, category.categoryId < 0)
        }.sortedByDescending { it.totalSpent }
    }
}