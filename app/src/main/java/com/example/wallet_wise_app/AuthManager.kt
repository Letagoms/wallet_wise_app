package com.example.wallet_wise_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class AuthManager(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun register(username: String, email: String, password: String): Boolean {
        if (username.isBlank() || email.isBlank() || password.isBlank()) return false

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USERNAME, username)
            put(DatabaseHelper.COLUMN_EMAIL, email)
            put(DatabaseHelper.COLUMN_PASSWORD, password)
        }

        val result = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        return result != -1L
    }

    fun login(username: String, password: String): Int {
        if (username.isBlank() || password.isBlank()) return -1

        val cursor = db.rawQuery(
            "SELECT ${DatabaseHelper.COLUMN_USER_ID} FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USERNAME} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?",
            arrayOf(username, password)
        )

        val userId = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID))
        } else {
            -1
        }
        cursor.close()
        return userId
    }
}