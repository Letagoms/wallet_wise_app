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

class CreateCategoryActivity : AppCompatActivity() {

    private lateinit var categoryManager: CategoryManager
    private var userId: Int = -1
    private var selectedColorResId: Int = android.R.color.holo_blue_dark
    private var selectedIconResId: Int = R.drawable.ic_home
    private var selectedColorView: ImageView? = null
    private var selectedIconCell: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        categoryManager = CategoryManager(this)
        userId = intent.getIntExtra("USER_ID", -1)

        val nameField = findViewById<EditText>(R.id.categoryNameInput)
        val minField = findViewById<EditText>(R.id.minGoalInput)
        val maxField = findViewById<EditText>(R.id.maxGoalInput)
        val slider = findViewById<Slider>(R.id.maxGoalSlider)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // Slider ↔ Max field
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
                    slider.value = (value / 100f).toInt() * 100f
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Color picker
        val colorPairs = listOf(
            findViewById<ImageView>(R.id.colorBlue) to android.R.color.holo_blue_dark,
            findViewById<ImageView>(R.id.colorGreen) to android.R.color.holo_green_dark,
            findViewById<ImageView>(R.id.colorRed) to android.R.color.holo_red_dark,
            findViewById<ImageView>(R.id.colorOrange) to android.R.color.holo_orange_dark,
            findViewById<ImageView>(R.id.colorPurple) to R.color.purple,
            findViewById<ImageView>(R.id.colorLightBlue) to R.color.light_blue,
            findViewById<ImageView>(R.id.colorGray) to R.color.gray
        )
        selectedColorView = colorPairs[0].first
        colorPairs.forEach { (view, colorResId) ->
            view.setOnClickListener {
                selectedColorView?.setImageResource(R.drawable.circle_color_normal)
                view.setImageResource(R.drawable.circle_color_selected)
                selectedColorView = view
                selectedColorResId = colorResId
            }
        }

        // Icon picker
        val iconPairs = listOf(
            findViewById<LinearLayout>(R.id.iconHouse) to R.drawable.ic_home,
            findViewById<LinearLayout>(R.id.iconCar) to R.drawable.ic_directions_car,
            findViewById<LinearLayout>(R.id.iconFood) to R.drawable.ic_restaurant,
            findViewById<LinearLayout>(R.id.iconGift) to R.drawable.ic_card_giftcard,
            findViewById<LinearLayout>(R.id.iconFlight) to R.drawable.ic_flight,
            findViewById<LinearLayout>(R.id.iconMedical) to R.drawable.ic_medical_services,
            findViewById<LinearLayout>(R.id.iconReceipt) to R.drawable.ic_receipt,
            findViewById<LinearLayout>(R.id.iconGames) to R.drawable.ic_sports_esports
        )
        selectedIconCell = iconPairs[0].first
        iconPairs.forEach { (cell, iconResId) ->
            cell.setOnClickListener {
                selectedIconCell?.background = ContextCompat.getDrawable(this, R.drawable.bg_icon_cell_normal)
                selectedIconCell?.findViewById<ImageView>(android.R.id.icon)
                    ?.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
                cell.background = ContextCompat.getDrawable(this, R.drawable.bg_icon_cell_selected)
                cell.findViewById<ImageView>(android.R.id.icon)
                    ?.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
                selectedIconCell = cell
                selectedIconResId = iconResId
            }
        }

        // Create button
        findViewById<Button>(R.id.btnCreateCategory).setOnClickListener {
            val name = nameField.text.toString().trim()
            val minGoal = minField.text.toString().toDoubleOrNull() ?: 0.0
            val maxGoal = maxField.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isBlank()) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (minGoal > maxGoal) {
                Toast.makeText(this, "Minimum cannot exceed maximum", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = Category(
                name = name,
                minGoal = minGoal,
                maxGoal = maxGoal,
                colorResId = selectedColorResId,
                iconResId = selectedIconResId,
                userId = userId
            )

            categoryManager.createCategory(category)
            Toast.makeText(this, "Category created!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}