package com.example.wallet_wise_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ExpenseDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "budgetapp.db"
        const val DATABASE_VERSION = 1

        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_ID = "id"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_RECEIPT_PATH = "receipt_path"
        const val COLUMN_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_EXPENSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_TIME TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_RECEIPT_PATH TEXT,
                $COLUMN_USER_ID INTEGER NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    // Force table creation on first use
    init {
        writableDatabase.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_EXPENSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_TIME TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_RECEIPT_PATH TEXT,
                $COLUMN_USER_ID INTEGER NOT NULL
            )
        """.trimIndent())
    }

    fun insertExpense(expense: Expense, userId: Int): Long {
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, expense.amount)
            put(COLUMN_DATE, expense.date)
            put(COLUMN_TIME, expense.time)
            put(COLUMN_CATEGORY, expense.category)
            put(COLUMN_DESCRIPTION, expense.description)
            put(COLUMN_RECEIPT_PATH, expense.receiptPath)
            put(COLUMN_USER_ID, userId)
        }
        return writableDatabase.insert(TABLE_EXPENSES, null, values)
    }

    fun getAllExpenses(userId: Int): List<Expense> {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_USER_ID = ? ORDER BY $COLUMN_DATE DESC, $COLUMN_TIME DESC",
            arrayOf(userId.toString())
        )
        return cursor.use {
            mutableListOf<Expense>().apply {
                while (it.moveToNext()) {
                    add(Expense(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                        amount = it.getDouble(it.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                        time = it.getString(it.getColumnIndexOrThrow(COLUMN_TIME)),
                        category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        receiptPath = it.getString(it.getColumnIndexOrThrow(COLUMN_RECEIPT_PATH))
                    ))
                }
            }
        }
    }
}