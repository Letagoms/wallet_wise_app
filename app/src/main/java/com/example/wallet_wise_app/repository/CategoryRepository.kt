// repository/CategoryRepository.kt
package com.example.wallet_wise_app.repository

import android.content.Context
import com.example.wallet_wise_app.database.DatabaseHelper
import com.example.wallet_wise_app.model.Category

class CategoryRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper.getInstance(context)

    fun save(category: Category): Category {
        val id = dbHelper.insertCategory(category)
        return category.copy(categoryId = id.toInt())
    }

    fun getAllCategories(): List<Category> {
        return dbHelper.getAllCategories()
    }

    fun getCategoriesByUserId(userId: Int): List<Category> {
        return dbHelper.getCategoriesByUserId(userId)
    }

    fun categoryExists(categoryName: String, userId: Int): Boolean {
        return dbHelper.categoryExists(categoryName, userId)
    }

    fun deleteCategory(categoryId: Int): Boolean {
        return dbHelper.deleteCategory(categoryId)
    }
}