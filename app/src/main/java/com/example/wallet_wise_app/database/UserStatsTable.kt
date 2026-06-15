// database/UserStatsTable.kt
package com.example.wallet_wise_app.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.wallet_wise_app.model.UserStats

object UserStatsTable {

    const val TABLE_NAME = "user_stats"
    private const val COLUMN_ID = "id"
    private const val COLUMN_USER_ID = "userId"
    private const val COLUMN_LOGIN_COUNT = "loginCount"
    private const val COLUMN_EXPENSE_COUNT = "expenseCount"
    private const val COLUMN_GOAL_COUNT = "goalCount"
    private const val COLUMN_LAST_LOGIN_DATE = "lastLoginDate"

    fun createTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER NOT NULL UNIQUE,
                $COLUMN_LOGIN_COUNT INTEGER DEFAULT 0,
                $COLUMN_EXPENSE_COUNT INTEGER DEFAULT 0,
                $COLUMN_GOAL_COUNT INTEGER DEFAULT 0,
                $COLUMN_LAST_LOGIN_DATE TEXT
            )
        """.trimIndent()
        db.execSQL(sql)
    }

    fun dropTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    fun insert(db: SQLiteDatabase, stats: UserStats): Long {
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, stats.userId)
            put(COLUMN_LOGIN_COUNT, stats.loginCount)
            put(COLUMN_EXPENSE_COUNT, stats.expenseCount)
            put(COLUMN_GOAL_COUNT, stats.goalCount)
            put(COLUMN_LAST_LOGIN_DATE, stats.lastLoginDate)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getByUserId(db: SQLiteDatabase, userId: Int): UserStats? {
        val cursor = db.query(TABLE_NAME, null, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()), null, null, null)

        return if (cursor.moveToFirst()) {
            UserStats(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                loginCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOGIN_COUNT)),
                expenseCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_COUNT)),
                goalCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_COUNT)),
                lastLoginDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_LOGIN_DATE))
            )
        } else {
            null
        }.also { cursor.close() }
    }

    fun updateLoginCount(db: SQLiteDatabase, userId: Int, count: Int, lastLoginDate: String) {
        val values = ContentValues().apply {
            put(COLUMN_LOGIN_COUNT, count)
            put(COLUMN_LAST_LOGIN_DATE, lastLoginDate)
        }
        db.update(TABLE_NAME, values, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }

    fun updateExpenseCount(db: SQLiteDatabase, userId: Int, count: Int) {
        val values = ContentValues().apply {
            put(COLUMN_EXPENSE_COUNT, count)
        }
        db.update(TABLE_NAME, values, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }

    fun updateGoalCount(db: SQLiteDatabase, userId: Int, count: Int) {
        val values = ContentValues().apply {
            put(COLUMN_GOAL_COUNT, count)
        }
        db.update(TABLE_NAME, values, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }
}