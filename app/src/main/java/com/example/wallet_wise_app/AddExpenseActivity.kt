package com.example.wallet_wise_app

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_wise_app.databinding.ActivityAddExpenseBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var dbHelper: DatabaseHelper
    private var receiptPath: String? = null
    private var userId: Int = -1
    private val PICK_IMAGE = 100
    private val CAPTURE_IMAGE = 101
    
    private var calendar = Calendar.getInstance()
    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)

        if (userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupCategorySpinner()

        // Handle pre-selected date from Calendar filter
        val preSelectedDate = intent.getStringExtra("SELECTED_DATE")
        if (preSelectedDate != null) {
            val date = dbDateFormat.parse(preSelectedDate)
            if (date != null) {
                calendar.time = date
            }
        }
        updateDateDisplay()

        // Make date clickable to change
        binding.tvDate.setOnClickListener {
            showDatePicker()
        }

        // Add Receipt click
        binding.btnAddReceipt.setOnClickListener {
            showImagePickerDialog()
        }

        // Add Expense button click
        binding.btnAddExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateDisplay()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun updateDateDisplay() {
        binding.tvDate.text = displayDateFormat.format(calendar.time)
    }

    private fun setupCategorySpinner() {
        val dbCategories = dbHelper.getAllCategories(userId)
        val categoryNames = mutableListOf<String>()
        categoryNames.add("Select category")
        categoryNames.addAll(dbCategories.map { it.name })
        
        if (categoryNames.size == 1) {
             categoryNames.addAll(listOf("Groceries", "Dining", "Transport", "Entertainment", "Other"))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Add Receipt")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> takePhoto()
                1 -> pickFromGallery()
            }
        }
        builder.show()
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAPTURE_IMAGE)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> {
                    val imageUri = data?.data
                    imageUri?.let { saveImageToInternal(it) }
                }
                CAPTURE_IMAGE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let { saveBitmapToInternal(it) }
                }
            }
        }
    }

    private fun saveImageToInternal(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            saveBitmapToInternal(bitmap)
            inputStream?.close()
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveBitmapToInternal(bitmap: Bitmap) {
        val filename = "receipt_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, filename)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
            receiptPath = file.absolutePath
            binding.ivReceiptPreview.visibility = android.view.View.VISIBLE
            binding.ivReceiptPreview.setImageBitmap(bitmap)
            binding.layoutUploadPrompt.visibility = android.view.View.GONE
            Toast.makeText(this, "Receipt added", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to save receipt", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveExpense() {
        val amountText = binding.etAmount.text.toString().replace("R", "").trim()
        if (amountText.isEmpty() || amountText == "0.00") {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            return
        }

        val category = binding.spinnerCategory.selectedItem.toString()
        if (category == "Select category") {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        val description = binding.etDescription.text.toString().ifEmpty { category }

        val expense = Expense(
            amount = amountText.toDouble(),
            date = dbDateFormat.format(calendar.time),
            time = currentTime,
            category = category,
            description = description,
            receiptPath = receiptPath
        )

        val result = dbHelper.insertExpense(expense, userId)
        if (result != -1L) {
            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show()
        }
    }
}