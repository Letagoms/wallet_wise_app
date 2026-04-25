package com.example.wallet_wise_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "wallet_wise_app.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_ID = "id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_RECEIPT_PATH = "receipt_path"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_TIME TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_RECEIPT_PATH TEXT
            )
        """.trimIndent()

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    fun insertExpense(expense: Expense): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, expense.amount)
            put(COLUMN_DATE, expense.date)
            put(COLUMN_TIME, expense.time)
            put(COLUMN_CATEGORY, expense.category)
            put(COLUMN_DESCRIPTION, expense.description)
            put(COLUMN_RECEIPT_PATH, expense.receiptPath)
        }
        return db.insert(TABLE_EXPENSES, null, values)
    }

    fun getAllExpenses(): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_EXPENSES ORDER BY $COLUMN_DATE DESC, $COLUMN_TIME DESC", null)

        cursor.use {
            while (it.moveToNext()) {
                val expense = Expense(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    amount = it.getDouble(it.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                    time = it.getString(it.getColumnIndexOrThrow(COLUMN_TIME)),
                    category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    receiptPath = it.getString(it.getColumnIndexOrThrow(COLUMN_RECEIPT_PATH))
                )
                expenses.add(expense)
            }
        }
        return expenses
    }

    fun getExpensesByDate(date: String): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_DATE = ? ORDER BY $COLUMN_TIME DESC",
            arrayOf(date)
        )

        cursor.use {
            while (it.moveToNext()) {
                val expense = Expense(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    amount = it.getDouble(it.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                    time = it.getString(it.getColumnIndexOrThrow(COLUMN_TIME)),
                    category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    receiptPath = it.getString(it.getColumnIndexOrThrow(COLUMN_RECEIPT_PATH))
                )
                expenses.add(expense)
            }
        }
        return expenses
    }
}