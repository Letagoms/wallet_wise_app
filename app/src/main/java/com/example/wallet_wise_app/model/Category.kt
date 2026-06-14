package com.example.wallet_wise_app.model

data class Category(
    val categoryId: Int = 0,           // Primary key (0 means new category)
    val categoryName: String,          // e.g., "Groceries", "Transport", "Food"
    val userId: Int                    // Which user this category belongs to
)