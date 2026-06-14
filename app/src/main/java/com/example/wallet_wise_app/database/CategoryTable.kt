package com.example.wallet_wise_app.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.wallet_wise_app.model.Category

object CategoryTable {

    // Table and column names
    const val TABLE_NAME = "categories"
    private const val COLUMN_CATEGORY_ID = "categoryId"
    private const val COLUMN_CATEGORY_NAME = "categoryName"
    private const val COLUMN_USER_ID = "userId"

    // Create table
    fun createTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT NOT NULL,
                $COLUMN_USER_ID INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(sql)
    }

    // Drop table (for upgrades)
    fun dropTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    // Insert new category
    fun insert(db: SQLiteDatabase, category: Category): Long {
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, category.categoryName)
            put(COLUMN_USER_ID, category.userId)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Get all categories
    fun getAll(db: SQLiteDatabase): List<Category> {
        val categories = mutableListOf<Category>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_CATEGORY_ID ASC")

        while (cursor.moveToNext()) {
            val category = Category(
                categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)),
                categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            )
            categories.add(category)
        }
        cursor.close()
        return categories
    }

    // Get categories by user ID
    fun getByUserId(db: SQLiteDatabase, userId: Int): List<Category> {
        val categories = mutableListOf<Category>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_CATEGORY_NAME ASC"
        )

        while (cursor.moveToNext()) {
            val category = Category(
                categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)),
                categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            )
            categories.add(category)
        }
        cursor.close()
        return categories
    }

    // Delete category by ID
    fun deleteById(db: SQLiteDatabase, categoryId: Int): Boolean {
        val rowsDeleted = db.delete(TABLE_NAME, "$COLUMN_CATEGORY_ID = ?", arrayOf(categoryId.toString()))
        return rowsDeleted > 0
    }

    // Check if category name already exists for this user
    fun exists(db: SQLiteDatabase, categoryName: String, userId: Int): Boolean {
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_CATEGORY_ID),
            "$COLUMN_CATEGORY_NAME = ? AND $COLUMN_USER_ID = ?",
            arrayOf(categoryName, userId.toString()),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}