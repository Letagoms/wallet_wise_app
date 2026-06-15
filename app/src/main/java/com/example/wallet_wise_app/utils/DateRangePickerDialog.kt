// utils/DateRangePickerDialog.kt
package com.example.wallet_wise_app.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*

class DateRangePickerDialog(
    private val context: Context,
    private val onRangeSelected: (startDate: String, endDate: String) -> Unit
) {

    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var selectingStart = true

    fun show() {
        showStartDatePicker()
    }

    private fun showStartDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            startDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            showEndDatePicker()
        }, year, month, day).show()
    }

    private fun showEndDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            endDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = dateFormat.format(startDate?.time)
            val end = dateFormat.format(endDate?.time)

            onRangeSelected(start, end)
        }, year, month, day).show()
    }
}