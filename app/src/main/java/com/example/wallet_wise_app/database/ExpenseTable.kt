// database/ExpenseTable.kt
package com.example.wallet_wise_app.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.wallet_wise_app.models.Expense

object ExpenseTable {

    const val TABLE_NAME = "expenses"
    private const val COLUMN_ID = "id"
    private const val COLUMN_NAME = "name"
    private const val COLUMN_AMOUNT = "amount"
    private const val COLUMN_CATEGORY = "category"  // NEW
    private const val COLUMN_DATE = "date"
    private const val COLUMN_START_TIME = "startTime"
    private const val COLUMN_END_TIME = "endTime"
    private const val COLUMN_PHOTO = "photo"
    private const val COLUMN_DESCRIPTION = "description"

    fun createTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_START_TIME TEXT NOT NULL,
                $COLUMN_END_TIME TEXT NOT NULL,
                $COLUMN_PHOTO TEXT,
                $COLUMN_DESCRIPTION TEXT
            )
        """.trimIndent()
        db.execSQL(sql)
    }

    fun dropTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    fun insert(db: SQLiteDatabase, expense: Expense): Long {
        val values = ContentValues().apply {
            put(COLUMN_NAME, expense.name)
            put(COLUMN_AMOUNT, expense.amount)
            put(COLUMN_CATEGORY, expense.category)  // NEW
            put(COLUMN_DATE, expense.date)
            put(COLUMN_START_TIME, expense.startTime)
            put(COLUMN_END_TIME, expense.endTime)
            put(COLUMN_PHOTO, expense.photo)
            put(COLUMN_DESCRIPTION, expense.description)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAll(db: SQLiteDatabase): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_ID DESC")

        while (cursor.moveToNext()) {
            val expense = Expense(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),  // NEW
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