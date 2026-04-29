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
        private const val DATABASE_VERSION = 2 // Incremented version to add categories table

        // Expenses Table
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_ID = "id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_RECEIPT_PATH = "receipt_path"

        // Categories Table (from SqlDb)
        private const val TABLE_CATEGORIES = "categories"
        private const val KEY_CAT_ID = "id"
        private const val KEY_CAT_NAME = "name"
        private const val KEY_MIN_GOAL = "minGoal"
        private const val KEY_MAX_GOAL = "maxGoal"
        private const val KEY_COLOR = "colorResId"
        private const val KEY_ICON = "iconResId"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createExpensesTable = """
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

        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $KEY_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_CAT_NAME TEXT NOT NULL,
                $KEY_MIN_GOAL REAL NOT NULL,
                $KEY_MAX_GOAL REAL NOT NULL,
                $KEY_COLOR INTEGER,
                $KEY_ICON INTEGER
            )
        """.trimIndent()

        db?.execSQL(createExpensesTable)
        db?.execSQL(createCategoriesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createCategoriesTable = """
                CREATE TABLE $TABLE_CATEGORIES (
                    $KEY_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $KEY_CAT_NAME TEXT NOT NULL,
                    $KEY_MIN_GOAL REAL NOT NULL,
                    $KEY_MAX_GOAL REAL NOT NULL,
                    $KEY_COLOR INTEGER,
                    $KEY_ICON INTEGER
                )
            """.trimIndent()
            db?.execSQL(createCategoriesTable)
        }
    }

    // --- Expense Methods ---

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

    // --- Category Methods (from SqlDb) ---

    fun insertCategory(category: Category): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_CAT_NAME, category.name)
            put(KEY_MIN_GOAL, category.minGoal)
            put(KEY_MAX_GOAL, category.maxGoal)
            put(KEY_COLOR, category.colorResId)
            put(KEY_ICON, category.iconResId)
        }
        return db.insert(TABLE_CATEGORIES, null, values)
    }

    fun getAllCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES", null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val category = Category(
                        id = it.getInt(it.getColumnIndexOrThrow(KEY_CAT_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(KEY_CAT_NAME)),
                        minGoal = it.getDouble(it.getColumnIndexOrThrow(KEY_MIN_GOAL)),
                        maxGoal = it.getDouble(it.getColumnIndexOrThrow(KEY_MAX_GOAL)),
                        colorResId = it.getInt(it.getColumnIndexOrThrow(KEY_COLOR)),
                        iconResId = it.getInt(it.getColumnIndexOrThrow(KEY_ICON))
                    )
                    categories.add(category)
                } while (it.moveToNext())
            }
        }
        return categories
    }
}
