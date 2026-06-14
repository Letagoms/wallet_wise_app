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

    // ========== UI COMPONENTS (Views) ==========
    // These are the actual screen elements the user interacts with
    // lateinit means "will be initialized before use" (in onCreate)

    private lateinit var etName: EditText           // User types expense name
    private lateinit var etAmount: EditText         // User types amount
    private lateinit var etCategory: EditText       // User types category
    private lateinit var etDate: EditText           // User types date
    private lateinit var etStartTime: EditText      // User types start time
    private lateinit var etEndTime: EditText        // User types end time
    private lateinit var etDescription: EditText    // User types description
    private lateinit var btnScanReceipt: Button     // Scans photo for data
    private lateinit var btnTakePhoto: Button       // Opens camera
    private lateinit var btnSelectPhoto: Button     // Opens gallery
    private lateinit var ivPhoto: ImageView         // Shows selected photo preview
    private lateinit var btnSubmit: Button          // Saves expense
    private lateinit var btnViewExpenses: Button    // Navigates to list

    // ========== DATA VARIABLES ==========
    private var selectedPhotoPath: String = ""      // Stores file path of selected photo
    private var currentPhotoBitmap: Bitmap? = null  // Stores image for preview and scanning
    private lateinit var expenseService: ExpenseService  // Service that handles business logic
    private var photoFile: java.io.File? = null     // Temporary file for camera photos

    // ========== CAMERA PERMISSION HANDLER ==========
    // Requests permission from user to use camera (required on Android 6.0+)
    // This launches a system dialog: "Allow app to take pictures?"
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()  // User said YES - open camera
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()  // User said NO
        }
    }

    // ========== CAMERA LAUNCHER ==========
    // Opens the device's camera app, takes photo, returns result
    // ActivityResultContracts.StartActivityForResult = start camera app and wait for result
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // This code runs AFTER user takes a photo and presses "OK" in camera app
        if (result.resultCode == RESULT_OK) {
            photoFile?.let { file ->
                if (file.exists()) {
                    // Save the photo path
                    selectedPhotoPath = file.absolutePath
                    // Convert file to Bitmap for preview
                    currentPhotoBitmap = PhotoHelper.loadBitmapFromPath(selectedPhotoPath)
                    // Display photo in ImageView
                    ivPhoto.setImageBitmap(currentPhotoBitmap)
                    ivPhoto.visibility = android.view.View.VISIBLE
                    Toast.makeText(this, "Photo taken! Tap Scan Receipt", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ========== GALLERY LAUNCHER ==========
    // Opens device's gallery app, user selects existing photo
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val photoUri: Uri? = data?.data  // URI of selected photo (content://...)
            photoUri?.let {
                // Copy photo to app storage and get file path
                selectedPhotoPath = PhotoHelper.savePhotoToStorage(this, it) ?: ""
                if (selectedPhotoPath.isNotEmpty()) {
                    // Convert URI to Bitmap for preview
                    currentPhotoBitmap = PhotoHelper.loadBitmapFromUri(this, it)
                    // Display photo in ImageView
                    ivPhoto.setImageBitmap(currentPhotoBitmap)
                    ivPhoto.visibility = android.view.View.VISIBLE
                    Toast.makeText(this, "Photo selected! Tap Scan Receipt", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ========== ON CREATE - ACTIVITY STARTUP ==========
    // Called ONCE when the screen is first created
    // Sets up the UI, buttons, and default values
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)  // Load the XML layout

        // Initialize the Service (business logic layer)
        expenseService = ExpenseService(this)

        // Connect UI variables to actual XML views using their IDs
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

        // Set default date to today's date (so user doesn't have to type it)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etDate.setText(dateFormat.format(Date()))

        // ========== BUTTON CLICK HANDLERS ==========

        // Scan Receipt Button: Extracts text from photo
        btnScanReceipt.setOnClickListener {
            if (currentPhotoBitmap != null) {
                scanReceipt()  // Photo exists, scan it
            } else {
                Toast.makeText(this, "Take or select a photo first", Toast.LENGTH_SHORT).show()
            }
        }

        // Take Photo Button: Opens camera (checks permission first)
        btnTakePhoto.setOnClickListener {
            // Check if permission already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera()  // Already have permission
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)  // Request permission
            }
        }

        // Choose Photo Button: Opens gallery
        btnSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

        // Submit Button: Saves expense
        btnSubmit.setOnClickListener {
            saveExpense()
        }

        // View Expenses Button: Navigates to list screen
        btnViewExpenses.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
    }

    // ========== OPEN CAMERA ==========
    // Creates temp file, sets up URI, launches camera app
    private fun launchCamera() {
        // Create a temporary file to save the photo
        photoFile = PhotoHelper.createPhotoFile(this)
        photoFile?.let { file ->
            // Get content:// URI for the file (required for Android 7.0+)
            val photoUri = PhotoHelper.getPhotoUri(this, file)
            if (photoUri != null) {
                // Create intent to open camera
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, photoUri)  // Tell camera where to save
                }
                cameraLauncher.launch(intent)  // Open camera
            } else {
                Toast.makeText(this, "Failed to create photo file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ========== SCAN RECEIPT (OCR) ==========
    // Uses ML Kit to extract text from photo
    // Auto-fills Name, Amount, Date fields
    private fun scanReceipt() {
        btnScanReceipt.isEnabled = false
        btnScanReceipt.text = "Scanning..."

        currentPhotoBitmap?.let { bitmap ->
            ReceiptScanner.scanReceipt(bitmap) { scannedData ->
                runOnUiThread {
                    // Auto-fill fields with scanned data
                    if (scannedData.amount.isNotEmpty()) {
                        etAmount.setText(scannedData.amount)
                    }
                    if (scannedData.date.isNotEmpty()) {
                        etDate.setText(scannedData.date)
                    }
                    if (scannedData.name.isNotEmpty()) {
                        etName.setText(scannedData.name)
                    }

                    // Show what was found
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

    // ========== SAVE EXPENSE ==========
    // Collects all form data, validates, calls Service to save
    // Runs in background thread (Thread) so UI doesn't freeze
    private fun saveExpense() {
        // Collect data from all input fields
        val name = etName.text.toString()
        val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
        val category = etCategory.text.toString()
        val date = etDate.text.toString()
        val startTime = etStartTime.text.toString()
        val endTime = etEndTime.text.toString()
        val description = etDescription.text.toString()

        // ========== BASIC VALIDATION ==========
        // (Simple checks before sending to Service)
        if (name.isBlank()) {
            Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show()
            return  // Stop here - don't save
        }

        if (amount <= 0) {
            Toast.makeText(this, "Valid amount required", Toast.LENGTH_SHORT).show()
            return
        }

        if (date.isBlank()) {
            Toast.makeText(this, "Date required", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable button while saving (prevent double-click)
        btnSubmit.isEnabled = false
        btnSubmit.text = "Saving..."

        // ========== BACKGROUND THREAD ==========
        // Database operations cannot run on UI thread (would freeze app)
        // Thread { } runs code in background
        Thread {
            try {
                // Call Service to save expense (Service has all business rules)
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

                // Switch back to UI thread to show result
                runOnUiThread {
                    Toast.makeText(this, "Saved! ID: ${savedExpense.id}", Toast.LENGTH_LONG).show()
                    finish()  // Close this screen, go back to previous screen
                }
            } catch (e: Exception) {
                // If anything fails, show error on UI thread
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "Save Expense"
                }
            }
        }.start()  // Start the background thread
    }
}