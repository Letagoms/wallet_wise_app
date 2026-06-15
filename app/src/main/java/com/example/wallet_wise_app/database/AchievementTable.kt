// database/AchievementTable.kt
package com.example.wallet_wise_app.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.wallet_wise_app.model.Achievement

object AchievementTable {

    const val TABLE_NAME = "achievements"
    private const val COLUMN_ID = "id"
    private const val COLUMN_NAME = "name"
    private const val COLUMN_DESCRIPTION = "description"
    private const val COLUMN_REQUIREMENT = "requirement"
    private const val COLUMN_REQUIRED_VALUE = "requiredValue"
    private const val COLUMN_ICON = "icon"
    private const val COLUMN_IS_UNLOCKED = "isUnlocked"
    private const val COLUMN_UNLOCKED_AT = "unlockedAt"
    private const val COLUMN_USER_ID = "userId"

    fun createTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_REQUIREMENT TEXT NOT NULL,
                $COLUMN_REQUIRED_VALUE INTEGER NOT NULL,
                $COLUMN_ICON TEXT NOT NULL,
                $COLUMN_IS_UNLOCKED INTEGER DEFAULT 0,
                $COLUMN_UNLOCKED_AT TEXT,
                $COLUMN_USER_ID INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(sql)
    }

    fun dropTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    fun insert(db: SQLiteDatabase, achievement: Achievement): Long {
        val values = ContentValues().apply {
            put(COLUMN_NAME, achievement.name)
            put(COLUMN_DESCRIPTION, achievement.description)
            put(COLUMN_REQUIREMENT, achievement.requirement)
            put(COLUMN_REQUIRED_VALUE, achievement.requiredValue)
            put(COLUMN_ICON, achievement.icon)
            put(COLUMN_IS_UNLOCKED, if (achievement.isUnlocked) 1 else 0)
            put(COLUMN_UNLOCKED_AT, achievement.unlockedAt)
            put(COLUMN_USER_ID, achievement.userId)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllByUserId(db: SQLiteDatabase, userId: Int): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        val cursor = db.query(TABLE_NAME, null, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()), null, null, "$COLUMN_REQUIRED_VALUE ASC")

        while (cursor.moveToNext()) {
            val achievement = Achievement(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                requirement = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUIREMENT)),
                requiredValue = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUIRED_VALUE)),
                icon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ICON)),
                isUnlocked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_UNLOCKED)) == 1,
                unlockedAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNLOCKED_AT)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            )
            achievements.add(achievement)
        }
        cursor.close()
        return achievements
    }

    fun updateUnlocked(db: SQLiteDatabase, achievementId: Int, unlockedAt: String) {
        val values = ContentValues().apply {
            put(COLUMN_IS_UNLOCKED, 1)
            put(COLUMN_UNLOCKED_AT, unlockedAt)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(achievementId.toString()))
    }

    fun getByRequirement(db: SQLiteDatabase, userId: Int, requirement: String): Achievement? {
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USER_ID = ? AND $COLUMN_REQUIREMENT = ?",
            arrayOf(userId.toString(), requirement),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            Achievement(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                requirement = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUIREMENT)),
                requiredValue = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUIRED_VALUE)),
                icon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ICON)),
                isUnlocked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_UNLOCKED)) == 1,
                unlockedAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNLOCKED_AT)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            )
        } else {
            null
        }.also { cursor.close() }
    }
}