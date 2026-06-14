package com.example.wallet_wise_app.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.wallet_wise_app.models.Expense

// OBJECT means this is a Singleton - only ONE instance exists in the entire app
// All functions here are like "static" methods - you call them directly without creating an instance
object ExpenseTable {
    const val TABLE_NAME = "expenses"  // Name of the table in the database

    // Column names - private because only this class needs to know them
    private const val COLUMN_ID = "id"                 // Primary key, auto-increments
    private const val COLUMN_NAME = "name"             // Expense name (e.g., "Coffee")
    private const val COLUMN_AMOUNT = "amount"         // Expense amount (e.g., 49.99)
    private const val COLUMN_CATEGORY = "category"     // might change to public
    private const val COLUMN_DATE = "date"             // Date of expense
    private const val COLUMN_START_TIME = "startTime"  // Start time
    private const val COLUMN_END_TIME = "endTime"      // End time
    private const val COLUMN_PHOTO = "photo"           // File path to photo
    private const val COLUMN_DESCRIPTION = "description" // Optional notes

    // ========== CREATE TABLE ==========
    // Called by DatabaseHelper.onCreate() when app is first installed
    // Executes raw SQL to create the table structure
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
        db.execSQL(sql)  // Execute the SQL command
    }

    // ========== DROP TABLE ==========
    // Called by DatabaseHelper.onUpgrade() when database version changes
    fun dropTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    // ========== INSERT (CREATE) ==========
    // Converts an Expense object into a database row
    // Takes an SQLiteDatabase connection and an Expense object
    // Returns the auto-generated ID of the new row
    fun insert(db: SQLiteDatabase, expense: Expense): Long {
        // ContentValues is like a Map: column name -> value
        val values = ContentValues().apply {
            put(COLUMN_NAME, expense.name)           // Put name into the "name" column
            put(COLUMN_AMOUNT, expense.amount)       // Put amount into the "amount" column
            put(COLUMN_CATEGORY, expense.category)   // Put category into "category" column
            put(COLUMN_DATE, expense.date)           // Put date into "date" column
            put(COLUMN_START_TIME, expense.startTime)
            put(COLUMN_END_TIME, expense.endTime)
            put(COLUMN_PHOTO, expense.photo)
            put(COLUMN_DESCRIPTION, expense.description)
        }
        // db.insert() returns the new row's ID, or -1 if failed
        return db.insert(TABLE_NAME, null, values)
    }

    // ========== SELECT ALL (READ) ==========
    // Retrieves all expenses from the database
    // Returns a List of Expense objects
    fun getAll(db: SQLiteDatabase): List<Expense> {
        val expenses = mutableListOf<Expense>()  // Empty list to fill with results
        // Query all rows, order by ID descending (newest first)
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_ID DESC")

        // Cursor = pointer to the result set (like an iterator)
        // moveToNext() returns false when no more rows
        while (cursor.moveToNext()) {
            // For each row, create an Expense object using column values
            val expense = Expense(
                // getColumnIndexOrThrow() finds the column number by name
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)),
                endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)),
                photo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            )
            expenses.add(expense)  // Add to list
        }
        cursor.close()  // Always close cursor to free memory
        return expenses
    }
}