// database/GoalTable.kt
package com.example.wallet_wise_app.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.wallet_wise_app.model.Goal

object GoalTable {

    // Table name and column names
    const val TABLE_NAME = "goals"
    private const val COLUMN_GOAL_ID = "goal_id"      // Primary Key (Auto-increment)
    private const val COLUMN_MINIMUM_GOAL = "minimum_goal"  // Integer
    private const val COLUMN_MAXIMUM_GOAL = "maximum_goal"  // Integer
    private const val COLUMN_USER_ID = "user_id"      // Foreign Key (links to users table)

    // Create table
    fun createTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_GOAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MINIMUM_GOAL INTEGER NOT NULL,
                $COLUMN_MAXIMUM_GOAL INTEGER NOT NULL,
                $COLUMN_USER_ID INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(sql)
    }

    // Drop table (for upgrades)
    fun dropTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    // Insert new goal
    fun insert(db: SQLiteDatabase, goal: Goal): Long {
        val values = ContentValues().apply {
            put(COLUMN_MINIMUM_GOAL, goal.minimumGoal)
            put(COLUMN_MAXIMUM_GOAL, goal.maximumGoal)
            put(COLUMN_USER_ID, goal.userId)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Get all goals
    fun getAll(db: SQLiteDatabase): List<Goal> {
        val goals = mutableListOf<Goal>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_GOAL_ID DESC")

        while (cursor.moveToNext()) {
            val goal = Goal(
                goalId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_ID)),
                minimumGoal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINIMUM_GOAL)),
                maximumGoal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAXIMUM_GOAL)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            )
            goals.add(goal)
        }
        cursor.close()
        return goals
    }

    // Get goals by user ID
    fun getByUserId(db: SQLiteDatabase, userId: Int): List<Goal> {
        val goals = mutableListOf<Goal>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_GOAL_ID DESC"
        )

        while (cursor.moveToNext()) {
            val goal = Goal(
                goalId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOAL_ID)),
                minimumGoal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINIMUM_GOAL)),
                maximumGoal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAXIMUM_GOAL)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            )
            goals.add(goal)
        }
        cursor.close()
        return goals
    }
}