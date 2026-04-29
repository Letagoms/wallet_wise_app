package com.example.wallet_wise_app

import android.content.Context

class CategoryManager(context: Context) {

    private val dbHelper = CategoryDatabaseHelper(context)

    fun createCategory(category: Category): Long {
        return dbHelper.insertCategory(category)
    }

    fun getCategories(userId: Int): List<Category> {
        return dbHelper.getAllCategories(userId)
    }

    fun getCategoryNames(userId: Int): List<String> {
        return dbHelper.getAllCategories(userId).map { it.name }
    }

    fun getCategoryIdsAndNames(userId: Int): List<Pair<Int, String>> {
        return dbHelper.getAllCategories(userId).map { Pair(it.id, it.name) }
    }
}