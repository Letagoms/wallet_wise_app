// utils/CustomBarChart.kt
package com.example.wallet_wise_app.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.Locale

class CustomBarChart(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val barPaint = Paint().apply {
        color = Color.parseColor("#2136F3")
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#333333")
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }

    private val axisPaint = Paint().apply {
        color = Color.parseColor("#CCCCCC")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val goalLinePaint = Paint().apply {
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val valuePaint = Paint().apply {
        color = Color.parseColor("#666666")
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private val labelPaint = Paint().apply {
        color = Color.parseColor("#333333")
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private data class BarData(
        val label: String,
        val value: Float,
        val color: Int
    )

    private val bars = mutableListOf<BarData>()
    private var minGoal: Float = 0f
    private var maxGoal: Float = 0f
    private var maxValue: Float = 0f

    fun setData(categoryNames: List<String>, categoryValues: List<Float>, minGoalValue: Float = 0f, maxGoalValue: Float = 0f) {
        bars.clear()
        for (i in categoryNames.indices) {
            bars.add(BarData(categoryNames[i], categoryValues[i], Color.parseColor("#2136F3")))
        }
        minGoal = minGoalValue
        maxGoal = maxGoalValue
        maxValue = if (categoryValues.isNotEmpty()) {
            (categoryValues.maxOrNull() ?: 0f).coerceAtLeast(maxGoalValue)
        } else {
            maxGoalValue
        }
        if (maxValue > 0) maxValue *= 1.1f
        else maxValue = 100f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (bars.isEmpty()) {
            textPaint.color = Color.parseColor("#999999")
            textPaint.textSize = 40f
            canvas.drawText("No data available", width / 2f, height / 2f, textPaint)
            return
        }

        val width = width.toFloat()
        val height = height.toFloat()
        val paddingLeft = 80f
        val paddingRight = 60f
        val paddingTop = 80f
        val paddingBottom = 80f
        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom

        // Draw Y-axis
        canvas.drawLine(paddingLeft, paddingTop, paddingLeft, height - paddingBottom, axisPaint)

        // Draw X-axis
        canvas.drawLine(paddingLeft, height - paddingBottom, width - paddingRight, height - paddingBottom, axisPaint)

        // Draw Y-axis labels
        val ySteps = 4
        for (i in 0..ySteps) {
            val value = (maxValue / ySteps) * i
            val y = height - paddingBottom - (chartHeight * (value / maxValue))
            if (value > 0) {
                val valueText = String.format(Locale.US, "R%.0f", value)
                textPaint.textSize = 28f
                textPaint.textAlign = Paint.Align.RIGHT
                canvas.drawText(valueText, paddingLeft - 10f, y + 8f, textPaint)
            }

            // Draw horizontal grid line
            axisPaint.color = Color.parseColor("#EEEEEE")
            canvas.drawLine(paddingLeft, y, width - paddingRight, y, axisPaint)
            axisPaint.color = Color.parseColor("#CCCCCC")
        }

        // Draw bars
        val barWidth = if (bars.isNotEmpty()) (chartWidth / bars.size) * 0.7f else 0f
        val barSpacing = if (bars.isNotEmpty()) (chartWidth / bars.size) * 0.3f else 0f
        val xStart = paddingLeft + barSpacing / 2

        for (i in bars.indices) {
            val bar = bars[i]
            val barHeight = (bar.value / maxValue) * chartHeight
            val left = xStart + i * (barWidth + barSpacing)
            val top = height - paddingBottom - barHeight
            val right = left + barWidth
            val bottom = height - paddingBottom

            barPaint.color = bar.color
            canvas.drawRect(left, top, right, bottom, barPaint)

            // Draw value on top of bar
            if (bar.value > 0) {
                valuePaint.textSize = 28f
                canvas.drawText(String.format(Locale.US, "R%.0f", bar.value), left + barWidth / 2, top - 10f, valuePaint)
            }

            // Draw label below bar
            labelPaint.textSize = 28f
            canvas.drawText(bar.label, left + barWidth / 2, height - paddingBottom + 35f, labelPaint)
        }

        // Draw goal lines
        if (minGoal > 0) {
            val minY = height - paddingBottom - (chartHeight * (minGoal / maxValue))
            goalLinePaint.color = Color.parseColor("#F44336")
            goalLinePaint.strokeWidth = 3f
            canvas.drawLine(paddingLeft, minY, width - paddingRight, minY, goalLinePaint)

            textPaint.color = Color.parseColor("#F44336")
            textPaint.textSize = 26f
            textPaint.textAlign = Paint.Align.LEFT
            canvas.drawText("Min Goal: R${minGoal.toInt()}", paddingLeft, minY - 10f, textPaint)
        }

        if (maxGoal > 0) {
            val maxY = height - paddingBottom - (chartHeight * (maxGoal / maxValue))
            goalLinePaint.color = Color.parseColor("#4CAF50")
            goalLinePaint.strokeWidth = 3f
            canvas.drawLine(paddingLeft, maxY, width - paddingRight, maxY, goalLinePaint)

            textPaint.color = Color.parseColor("#4CAF50")
            textPaint.textSize = 26f
            textPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Max Goal: R${maxGoal.toInt()}", width - paddingRight, maxY - 10f, textPaint)
        }
    }
}