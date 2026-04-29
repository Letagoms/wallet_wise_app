package com.example.wallet_wise_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CategoryDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "budgetapp.db"
        const val DATABASE_VERSION = 1

        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_MIN_GOAL = "minGoal"
        const val COLUMN_MAX_GOAL = "maxGoal"
        const val COLUMN_COLOR = "colorResId"
        const val COLUMN_ICON = "iconResId"
        const val COLUMN_USER_ID = "userId"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_CATEGORIES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_MIN_GOAL REAL NOT NULL DEFAULT 0,
                $COLUMN_MAX_GOAL REAL NOT NULL DEFAULT 0,
                $COLUMN_COLOR INTEGER,
                $COLUMN_ICON INTEGER,
                $COLUMN_USER_ID INTEGER NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }

    init {
        writableDatabase // Forces table creation immediately
    }

    fun insertCategory(category: Category): Long {
        val values = ContentValues().apply {
            put(COLUMN_NAME, category.name)
            put(COLUMN_MIN_GOAL, category.minGoal)
            put(COLUMN_MAX_GOAL, category.maxGoal)
            put(COLUMN_COLOR, category.colorResId)
            put(COLUMN_ICON, category.iconResId)
            put(COLUMN_USER_ID, category.userId)
        }
        return writableDatabase.insert(TABLE_CATEGORIES, null, values)
    }

    fun getAllCategories(userId: Int): List<Category> {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_CATEGORIES WHERE $COLUMN_USER_ID = ?",
            arrayOf(userId.toString())
        )
        return cursor.use {
            mutableListOf<Category>().apply {
                while (it.moveToNext()) {
                    add(Category(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                        minGoal = it.getDouble(it.getColumnIndexOrThrow(COLUMN_MIN_GOAL)),
                        maxGoal = it.getDouble(it.getColumnIndexOrThrow(COLUMN_MAX_GOAL)),
                        colorResId = it.getInt(it.getColumnIndexOrThrow(COLUMN_COLOR)),
                        iconResId = it.getInt(it.getColumnIndexOrThrow(COLUMN_ICON)),
                        userId = it.getInt(it.getColumnIndexOrThrow(COLUMN_USER_ID))
                    ))
                }
            }
        }
    }
}