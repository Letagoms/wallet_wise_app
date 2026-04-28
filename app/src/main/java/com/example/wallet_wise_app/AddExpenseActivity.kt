package com.example.wallet_wise_app

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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
    private val PICK_IMAGE = 100
    private val CAPTURE_IMAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Set up category dropdown
        val categories = arrayOf(
            "Select category",
            "Groceries",
            "Dining",
            "Transport",
            "Entertainment",
            "Housing",
            "Shopping",
            "Healthcare",
            "Other"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        // Set current date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val today = Date()
        binding.tvDate.text = displayDateFormat.format(today)

        // Add Receipt click
        binding.btnAddReceipt.setOnClickListener {
            showImagePickerDialog()
        }

        // Add Expense button click
        binding.btnAddExpense.setOnClickListener {
            saveExpense(dateFormat.format(today))
        }
    }

    // Shows a popup dialog with two options: Take Photo or Choose from Gallery
    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Add Receipt")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> takePhoto()       // User chose "Take Photo"
                1 -> pickFromGallery() // User chose "Choose from Gallery"
            }
        }
        builder.show()
    }

    // Opens the phone's camera app to take a photo
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAPTURE_IMAGE)
    }

    // Opens the phone's gallery to pick an existing photo
    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    // This method runs when we get a result back from camera or gallery
    // requestCode = which action returned (PICK_IMAGE or CAPTURE_IMAGE)
    // resultCode = was it successful or did user cancel?
    // data = the actual image data
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Only process if user didn't cancel
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // User picked from gallery - get the image URI
                PICK_IMAGE -> {
                    val imageUri = data?.data
                    imageUri?.let {
                        saveImageToInternal(it)
                    }
                }
                // User took a photo - get the bitmap from camera
                CAPTURE_IMAGE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        saveBitmapToInternal(it)
                    }
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

            // Show preview
            binding.ivReceiptPreview.visibility = android.view.View.VISIBLE
            binding.ivReceiptPreview.setImageBitmap(bitmap)
            binding.layoutUploadPrompt.visibility = android.view.View.GONE

            Toast.makeText(this, "Receipt added", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to save receipt", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveExpense(currentDate: String) {
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
         // Get current time in "HH:mm" format
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())

        val description = binding.etDescription.text.toString().ifEmpty { category }

        //expense object created
        val expense = Expense(
            amount = amountText.toDouble(),
            date = currentDate,
            time = currentTime,
            category = category,
            description = description,
            receiptPath = receiptPath
        )
         //SAVE TO DATABASE
        val result = dbHelper.insertExpense(expense)
        if (result != -1L) {
            // Success! Show confirmation and go back to list
            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // Failed to save
            Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show()
        }
    }
}