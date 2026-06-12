package com.example.wallet_wise_app.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wallet_wise_app.models.Category
import com.example.wallet_wise_app.models.Expense

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "wallet_wise_app.db"
        private const val DATABASE_VERSION = 3 // Incremented for user integration

        // Users Table
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        // Expenses Table
        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_EXP_ID = "id"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_RECEIPT_PATH = "receipt_path"
        const val COLUMN_EXP_USER_ID = "user_id"

        // Categories Table
        const val TABLE_CATEGORIES = "categories"
        const val KEY_CAT_ID = "id"
        const val KEY_CAT_NAME = "name"
        const val KEY_MIN_GOAL = "minGoal"
        const val KEY_MAX_GOAL = "maxGoal"
        const val KEY_COLOR = "colorResId"
        const val KEY_ICON = "iconResId"
        const val COLUMN_CAT_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULLval databaseName = "wallet_wise_app.db"
val deleted = context.deleteDatabase(databaseName)
if (deleted) {
    // Database successfully deleted
}val databaseName = "wallet_wise_app.db"
val deleted = context.deleteDatabase(databaseName)
if (deleted) {
    // Database successfully deleted
}val databaseName = "wallet_wise_app.db"
val deleted = context.deleteDatabase(databaseName)
if (deleted) {
    // Database successfully deleted
}val databaseName = "wallet_wise_app.db"
val deleted = context.deleteDatabase(databaseName)
if (deleted) {
    // Database successfully deleted
}
            )
        """.trimIndent()

        val createExpensesTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_EXP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_TIME TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_RECEIPT_PATH TEXT,
                $COLUMN_EXP_USER_ID INTEGER,
                FOREIGN KEY($COLUMN_EXP_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()

        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $KEY_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_CAT_NAME TEXT NOT NULL,
                $KEY_MIN_GOAL REAL NOT NULL,
                $KEY_MAX_GOAL REAL NOT NULL,
                $KEY_COLOR INTEGER,
                $KEY_ICON INTEGER,
                $COLUMN_CAT_USER_ID INTEGER,
                FOREIGN KEY($COLUMN_CAT_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()

        db?.execSQL(createUsersTable)
        db?.execSQL(createExpensesTable)
        db?.execSQL(createCategoriesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            onCreate(db)
        }
    }

    // --- User Methods handled via AuthManager ---

    // --- Expense Methods ---

    fun insertExpense(expense: Expense, userId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, expense.amount)
            put(COLUMN_DATE, expense.date)
            put(COLUMN_TIME, expense.time)
            put(COLUMN_CATEGORY, expense.category)
            put(COLUMN_DESCRIPTION, expense.description)
            put(COLUMN_RECEIPT_PATH, expense.receiptPath)
            put(COLUMN_EXP_USER_ID, userId)
        }
        return db.insert(TABLE_EXPENSES, null, values)
    }

    fun getAllExpenses(userId: Int): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_EXP_USER_ID = ? ORDER BY $COLUMN_DATE DESC, $COLUMN_TIME DESC",
            arrayOf(userId.toString())
        )

        cursor.use {
            while (it.moveToNext()) {
                val expense = Expense(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_EXP_ID)),
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

    fun getExpensesByDate(userId: Int, date: String): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_EXP_USER_ID = ? AND $COLUMN_DATE = ? ORDER BY $COLUMN_TIME DESC",
            arrayOf(userId.toString(), date)
        )

        cursor.use {
            while (it.moveToNext()) {
                val expense = Expense(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_EXP_ID)),
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

    fun getSpentPerCategory(userId: Int): Map<String, Double> {
        val spentMap = mutableMapOf<String, Double>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_CATEGORY, SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_EXP_USER_ID = ? GROUP BY $COLUMN_CATEGORY",
            arrayOf(userId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val categoryName = it.getString(0)
                val totalAmount = it.getDouble(1)
                spentMap[categoryName] = totalAmount
            }
        }
        return spentMap
    }

    // --- Category Methods ---

    fun insertCategory(category: Category, userId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_CAT_NAME, category.name)
            put(KEY_MIN_GOAL, category.minGoal)
            put(KEY_MAX_GOAL, category.maxGoal)
            put(KEY_COLOR, category.colorResId)
            put(KEY_ICON, category.iconResId)
            put(COLUMN_CAT_USER_ID, userId)
        }
        return db.insert(TABLE_CATEGORIES, null, values)
    }

    fun getAllCategories(userId: Int): List<Category> {
        val categories = mutableListOf<Category>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CATEGORIES WHERE $COLUMN_CAT_USER_ID = ?",
            arrayOf(userId.toString())
        )

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