// screens/CreateExpenseActivity.kt
package com.example.wallet_wise_app.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.services.ExpenseService
import com.example.wallet_wise_app.utils.PhotoHelper
import com.example.wallet_wise_app.utils.ReceiptScanner
import java.text.SimpleDateFormat
import java.util.*

class CreateExpenseActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAmount: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnScanReceipt: Button
    private lateinit var btnTakePhoto: Button
    private lateinit var btnSelectPhoto: Button
    private lateinit var ivPhoto: ImageView
    private lateinit var btnSubmit: Button
    private lateinit var btnViewExpenses: Button

    private var selectedPhotoPath: String = ""
    private var currentPhotoBitmap: Bitmap? = null
    private lateinit var expenseService: ExpenseService
    private var photoFile: java.io.File? = null

    // Camera permission launcher
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            photoFile?.let { file ->
                if (file.exists()) {
                    selectedPhotoPath = file.absolutePath
                    currentPhotoBitmap = PhotoHelper.loadBitmapFromPath(selectedPhotoPath)
                    ivPhoto.setImageBitmap(currentPhotoBitmap)
                    ivPhoto.visibility = android.view.View.VISIBLE
                    Toast.makeText(this, "Photo taken! Tap Scan Receipt", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Gallery launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val photoUri: Uri? = data?.data
            photoUri?.let {
                selectedPhotoPath = PhotoHelper.savePhotoToStorage(this, it) ?: ""
                if (selectedPhotoPath.isNotEmpty()) {
                    currentPhotoBitmap = PhotoHelper.loadBitmapFromUri(this, it)
                    ivPhoto.setImageBitmap(currentPhotoBitmap)
                    ivPhoto.visibility = android.view.View.VISIBLE
                    Toast.makeText(this, "Photo selected! Tap Scan Receipt", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        expenseService = ExpenseService(this)

        etName = findViewById(R.id.etName)
        etAmount = findViewById(R.id.etAmount)
        etCategory = findViewById(R.id.etCategory)
        etDate = findViewById(R.id.etDate)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        etDescription = findViewById(R.id.etDescription)
        btnScanReceipt = findViewById(R.id.btnScanReceipt)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        ivPhoto = findViewById(R.id.ivPhoto)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnViewExpenses = findViewById(R.id.btnViewExpenses)

        // Set default date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etDate.setText(dateFormat.format(Date()))

        btnScanReceipt.setOnClickListener {
            if (currentPhotoBitmap != null) {
                scanReceipt()
            } else {
                Toast.makeText(this, "Take or select a photo first", Toast.LENGTH_SHORT).show()
            }
        }

        btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        btnSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

        btnSubmit.setOnClickListener {
            saveExpense()
        }

        btnViewExpenses.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
    }

    private fun launchCamera() {
        photoFile = PhotoHelper.createPhotoFile(this)
        photoFile?.let { file ->
            val photoUri = PhotoHelper.getPhotoUri(this, file)
            if (photoUri != null) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                }
                cameraLauncher.launch(intent)
            } else {
                Toast.makeText(this, "Failed to create photo file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scanReceipt() {
        btnScanReceipt.isEnabled = false
        btnScanReceipt.text = "Scanning..."

        currentPhotoBitmap?.let { bitmap ->
            ReceiptScanner.scanReceipt(bitmap) { scannedData ->
                runOnUiThread {
                    if (scannedData.amount.isNotEmpty()) {
                        etAmount.setText(scannedData.amount)
                    }
                    if (scannedData.date.isNotEmpty()) {
                        etDate.setText(scannedData.date)
                    }
                    if (scannedData.name.isNotEmpty()) {
                        etName.setText(scannedData.name)
                    }

                    val message = buildString {
                        if (scannedData.amount.isNotEmpty()) append("Amount: R${scannedData.amount} ")
                        if (scannedData.date.isNotEmpty()) append("Date: ${scannedData.date} ")
                        if (scannedData.name.isNotEmpty()) append("Store: ${scannedData.name}")
                    }

                    Toast.makeText(
                        this,
                        if (message.isBlank()) "No data found" else message,
                        Toast.LENGTH_LONG
                    ).show()

                    btnScanReceipt.isEnabled = true
                    btnScanReceipt.text = "Scan Receipt"
                }
            }
        }
    }

    private fun saveExpense() {
        val name = etName.text.toString()
        val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
        val category = etCategory.text.toString()
        val date = etDate.text.toString()
        val startTime = etStartTime.text.toString()
        val endTime = etEndTime.text.toString()
        val description = etDescription.text.toString()

        if (name.isBlank()) {
            Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show()
            return
        }

        if (amount <= 0) {
            Toast.makeText(this, "Valid amount required", Toast.LENGTH_SHORT).show()
            return
        }

        if (date.isBlank()) {
            Toast.makeText(this, "Date required", Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled = false
        btnSubmit.text = "Saving..."

        Thread {
            try {
                val savedExpense = expenseService.addExpense(
                    name = name,
                    amount = amount,
                    category = category,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    photo = selectedPhotoPath,
                    description = description
                )

                runOnUiThread {
                    Toast.makeText(this, "Saved! ID: ${savedExpense.id}", Toast.LENGTH_LONG).show()
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "Save Expense"
                }
            }
        }.start()
    }
}