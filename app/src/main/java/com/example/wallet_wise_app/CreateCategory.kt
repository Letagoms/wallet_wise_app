package com.example.wallet_wise_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.slider.Slider

class CreateCategory : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    // Tracks current selections
    private var selectedColorResId: Int = android.R.color.holo_blue_dark  // default blue
    private var selectedIconResId:  Int = R.drawable.ic_home               // default house

    // Color circle views
    private lateinit var colorViews: List<Pair<ImageView, Int>>   // view → colorResId

    // Icon cell views
    private lateinit var iconCells: List<Pair<LinearLayout, Int>> // view → iconResId

    // Currently highlighted views
    private var selectedColorView: ImageView?   = null
    private var selectedIconCell:  LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        dbHelper = DatabaseHelper(this)

        // ── Field references ──
        val nameField:   EditText = findViewById(R.id.categoryNameInput)
        val minField:    EditText = findViewById(R.id.minGoalInput)
        val maxField:    EditText = findViewById(R.id.maxGoalInput)
        val slider:      Slider   = findViewById(R.id.maxGoalSlider)
        val createButton: Button  = findViewById(R.id.btnCreateCategory)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        //
        // SLIDER FOR MAX GOAL FIELD (two-way binding)
        //
        slider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                maxField.setText("%.2f".format(value))
                maxField.setSelection(maxField.text.length)
            }
        }

        maxField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toFloatOrNull() ?: return
                if (value in slider.valueFrom..slider.valueTo) {
                    // Round to nearest stepSize (100) to avoid slider crash
                    val stepped = (value / 100f).toInt() * 100f
                    slider.value = stepped
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //
        // COLOR PICKER
        //
        colorViews = listOf(
            Pair(findViewById(R.id.colorBlue),      android.R.color.holo_blue_dark),
            Pair(findViewById(R.id.colorGreen),     android.R.color.holo_green_dark),
            Pair(findViewById(R.id.colorRed),       android.R.color.holo_red_dark),
            Pair(findViewById(R.id.colorOrange),    android.R.color.holo_orange_dark),
            Pair(findViewById(R.id.colorPurple),    R.color.purple),
            Pair(findViewById(R.id.colorLightBlue), R.color.light_blue),
            Pair(findViewById(R.id.colorGray),      R.color.gray)
        )

        // Set blue as default selected
        selectedColorView = colorViews[0].first
        selectedColorResId = colorViews[0].second

        colorViews.forEach { (view, colorResId) ->
            view.setOnClickListener {
                // Remove ring from previously selected
                selectedColorView?.setImageResource(R.drawable.circle_color_normal)
                // Apply ring to newly selected
                view.setImageResource(R.drawable.circle_color_selected)
                selectedColorView  = view
                selectedColorResId = colorResId
            }
        }

        //
        // ICON GRID
        //
        iconCells = listOf(
            Pair(findViewById(R.id.iconHouse),   R.drawable.ic_home),
            Pair(findViewById(R.id.iconCar),     R.drawable.ic_directions_car),
            Pair(findViewById(R.id.iconFood),    R.drawable.ic_restaurant),
            Pair(findViewById(R.id.iconGift),    R.drawable.ic_card_giftcard),
            Pair(findViewById(R.id.iconFlight),  R.drawable.ic_flight),
            Pair(findViewById(R.id.iconMedical), R.drawable.ic_medical_services),
            Pair(findViewById(R.id.iconReceipt), R.drawable.ic_receipt),
            Pair(findViewById(R.id.iconGames),   R.drawable.ic_sports_esports)
        )

        // Set house as default selected
        selectedIconCell  = iconCells[0].first
        selectedIconResId = iconCells[0].second

        iconCells.forEach { (cell, iconResId) ->
            cell.setOnClickListener {
                // Remove highlight from previously selected
                selectedIconCell?.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_icon_cell_normal)
                // Apply highlight to newly selected
                cell.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_icon_cell_selected)
                // Also update the icon tint: selected = blue, others = gray
                selectedIconCell?.findViewById<ImageView>(android.R.id.icon)
                    ?.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
                cell.findViewById<ImageView>(android.R.id.icon)
                    ?.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_blue_dark))

                selectedIconCell  = cell
                selectedIconResId = iconResId
            }
        }

        //
        // CREATE BUTTON
        //
        createButton.setOnClickListener {
            val name    = nameField.text.toString().trim()
            val minGoal = minField.text.toString().toDoubleOrNull() ?: 0.0
            val maxGoal = maxField.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isBlank()) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (minGoal > maxGoal) {
                Toast.makeText(this, "Minimum goal cannot exceed maximum goal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = Category(
                name       = name,
                minGoal    = minGoal,
                maxGoal    = maxGoal,
                colorResId = selectedColorResId,
                iconResId  = selectedIconResId
            )

            dbHelper.insertCategory(category)
            Toast.makeText(this, "Category created successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}