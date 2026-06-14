// model/User.kt
package com.example.wallet_wise_app.model

data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,  // In real app, store hashed password
    val createdAt: String
)