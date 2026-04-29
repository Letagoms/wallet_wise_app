package com.example.wallet_wise_app

import android.app.Activity
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
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var dbHelper: ExpenseDatabaseHelper
    private var receiptPath: String? = null
    private var userId: Int = -1
    private val PICK_IMAGE = 100
    private val CAPTURE_IMAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ExpenseDatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)

        val categories = arrayOf(
            "Select category", "Groceries", "Dining", "Transport",
            "Entertainment", "Housing", "Shopping", "Healthcare", "Other"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val today = Date()
        binding.tvDate.text = displayDateFormat.format(today)

        binding.btnAddReceipt.setOnClickListener { showImagePickerDialog() }
        binding.btnAddExpense.setOnClickListener { saveExpense(dateFormat.format(today)) }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Add Receipt")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAPTURE_IMAGE)
                1 -> startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_IMAGE)
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> data?.data?.let { saveImageToInternal(it) }
                CAPTURE_IMAGE -> (data?.extras?.get("data") as? Bitmap)?.let { saveBitmapToInternal(it) }
            }
        }
    }

    private fun saveImageToInternal(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        bitmap?.let { saveBitmapToInternal(it) }
    }

    private fun saveBitmapToInternal(bitmap: Bitmap) {
        val file = File(filesDir, "receipt_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        }
        receiptPath = file.absolutePath
        binding.ivReceiptPreview.visibility = android.view.View.VISIBLE
        binding.ivReceiptPreview.setImageBitmap(bitmap)
        binding.layoutUploadPrompt.visibility = android.view.View.GONE
        Toast.makeText(this, "Receipt added", Toast.LENGTH_SHORT).show()
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

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        val description = binding.etDescription.text.toString().ifEmpty { category }

        val expense = Expense(
            amount = amountText.toDouble(),
            date = currentDate,
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