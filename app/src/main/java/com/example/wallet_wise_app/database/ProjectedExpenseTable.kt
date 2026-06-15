// database/ProjectedExpenseTable.kt
package com.example.wallet_wise_app.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.wallet_wise_app.model.ProjectedExpense

object ProjectedExpenseTable {

    const val TABLE_NAME = "projected_expenses"
    private const val COLUMN_ID = "id"
    private const val COLUMN_NAME = "name"
    private const val COLUMN_AMOUNT = "amount"
    private const val COLUMN_DATE = "date"
    private const val COLUMN_USER_ID = "userId"

    fun createTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_USER_ID INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(sql)
    }

    fun dropTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    fun insert(db: SQLiteDatabase, expense: ProjectedExpense): Long {
        val values = ContentValues().apply {
            put(COLUMN_NAME, expense.name)
            put(COLUMN_AMOUNT, expense.amount)
            put(COLUMN_DATE, expense.date)
            put(COLUMN_USER_ID, expense.userId)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAll(db: SQLiteDatabase): List<ProjectedExpense> {
        val expenses = mutableListOf<ProjectedExpense>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_DATE ASC")

        while (cursor.moveToNext()) {
            val expense = ProjectedExpense(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            )
            expenses.add(expense)
        }
        cursor.close()
        return expenses
    }

    fun getByUserId(db: SQLiteDatabase, userId: Int): List<ProjectedExpense> {
        val expenses = mutableListOf<ProjectedExpense>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_DATE ASC"
        )

        while (cursor.moveToNext()) {
            val expense = ProjectedExpense(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            )
            expenses.add(expense)
        }
        cursor.close()
        return expenses
    }

    fun getByMonth(db: SQLiteDatabase, userId: Int, year: Int, month: Int): List<ProjectedExpense> {
        val monthStr = String.format("%04d-%02d", year, month)
        val expenses = mutableListOf<ProjectedExpense>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USER_ID = ? AND $COLUMN_DATE LIKE ?",
            arrayOf(userId.toString(), "$monthStr%"),
            null,
            null,
            "$COLUMN_DATE ASC"
        )

        while (cursor.moveToNext()) {
            val expense = ProjectedExpense(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            )
            expenses.add(expense)
        }
        cursor.close()
        return expenses
    }

    fun delete(db: SQLiteDatabase, id: Int): Boolean {
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString())) > 0
    }
}