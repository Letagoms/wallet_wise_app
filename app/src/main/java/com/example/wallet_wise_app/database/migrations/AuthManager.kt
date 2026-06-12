package com.example.wallet_wise_app.database.migrations

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.wallet_wise_app.database.DatabaseHelper

class AuthManager(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun register(username: String, email: String, password: String): Boolean {
        if (username.isBlank() || email.isBlank() || password.isBlank()) return false

        val values = ContentValues().apply {
            put(DatabaseHelper.Companion.COLUMN_USERNAME, username)
            put(DatabaseHelper.Companion.COLUMN_EMAIL, email)
            put(DatabaseHelper.Companion.COLUMN_PASSWORD, password)
        }

        val result = db.insert(DatabaseHelper.Companion.TABLE_USERS, null, values)
        return result != -1L
    }

    fun login(username: String, password: String): Int {
        if (username.isBlank() || password.isBlank()) return -1

        val cursor = db.rawQuery(
            "SELECT ${DatabaseHelper.Companion.COLUMN_USER_ID} FROM ${DatabaseHelper.Companion.TABLE_USERS} WHERE ${DatabaseHelper.Companion.COLUMN_USERNAME} = ? AND ${DatabaseHelper.Companion.COLUMN_PASSWORD} = ?",
            arrayOf(username, password)
        )

        val userId = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.Companion.COLUMN_USER_ID))
        } else {
            -1
        }
        cursor.close()
        return userId
    }
}