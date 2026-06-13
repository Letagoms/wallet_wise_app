// database/DatabaseHelper.kt
package com.example.wallet_wise_app.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wallet_wise_app.models.Expense

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wallet_wise.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_START_TIME = "startTime"
        private const val COLUMN_END_TIME = "endTime"
        private const val COLUMN_PHOTO = "photo"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_START_TIME TEXT NOT NULL,
                $COLUMN_END_TIME TEXT NOT NULL,
                $COLUMN_PHOTO TEXT,
                $COLUMN_DESCRIPTION TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    fun insertExpense(name: String, amount: Double, date: String, startTime: String, endTime: String, photo: String, description: String): Long {
        val db = writableDatabase
        val values = android.content.ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DATE, date)
            put(COLUMN_START_TIME, startTime)
            put(COLUMN_END_TIME, endTime)
            put(COLUMN_PHOTO, photo)
            put(COLUMN_DESCRIPTION, description)
        }
        return db.insert(TABLE_EXPENSES, null, values)
    }

    fun getAllExpenses(): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.query(TABLE_EXPENSES, null, null, null, null, null, "$COLUMN_ID DESC")

        while (cursor.moveToNext()) {
            val expense = Expense(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)),
                endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)),
                photo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            )
            expenses.add(expense)
        }
        cursor.close()
        return expenses
    }
}