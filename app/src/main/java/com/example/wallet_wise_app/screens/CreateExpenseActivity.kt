// screens/CreateExpenseActivity.kt
package com.example.wallet_wise_app.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wallet_wise_app.R
import com.example.wallet_wise_app.services.ExpenseService
import com.example.wallet_wise_app.utils.ReceiptScanner
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CreateExpenseActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnScanReceipt: Button
    private lateinit var btnSelectPhoto: Button
    private lateinit var ivPhoto: ImageView
    private lateinit var btnSubmit: Button
    private lateinit var btnViewExpenses: Button

    private var selectedPhotoPath: String = ""
    private var currentPhotoBitmap: Bitmap? = null
    private lateinit var expenseService: ExpenseService

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val photoUri: Uri? = data?.data
            photoUri?.let {
                selectedPhotoPath = savePhotoToStorage(it)
                if (selectedPhotoPath.isNotEmpty()) {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                    currentPhotoBitmap = bitmap
                    ivPhoto.setImageBitmap(bitmap)
                    ivPhoto.visibility = android.view.View.VISIBLE
                    Toast.makeText(this, "Photo selected! Tap Scan to extract data", Toast.LENGTH_LONG).show()
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
        etDate = findViewById(R.id.etDate)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        etDescription = findViewById(R.id.etDescription)
        btnScanReceipt = findViewById(R.id.btnScanReceipt)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        ivPhoto = findViewById(R.id.ivPhoto)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnViewExpenses = findViewById(R.id.btnViewExpenses)

        btnScanReceipt.setOnClickListener {
            if (currentPhotoBitmap != null) {
                scanReceipt()
            } else {
                Toast.makeText(this, "Please select a receipt photo first", Toast.LENGTH_SHORT).show()
            }
        }

        btnSelectPhoto.setOnClickListener {
            openPhotoPicker()
        }

        btnSubmit.setOnClickListener {
            saveExpense()
        }

        btnViewExpenses.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
    }

    private fun openPhotoPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        photoPickerLauncher.launch(intent)
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

                    val message = "Scanned: ${if (scannedData.amount.isNotEmpty()) "Amount: R${scannedData.amount} " else ""}" +
                            "${if (scannedData.date.isNotEmpty()) "Date: ${scannedData.date} " else ""}"

                    Toast.makeText(this, message.ifEmpty { "No data found. Please enter manually" }, Toast.LENGTH_LONG).show()

                    btnScanReceipt.isEnabled = true
                    btnScanReceipt.text = "📷 Scan Receipt"
                }
            }
        }
    }

    private fun savePhotoToStorage(photoUri: Uri): String {
        return try {
            val inputStream = contentResolver.openInputStream(photoUri)
            val fileName = "${UUID.randomUUID()}.jpg"
            val photoFile = File(filesDir, fileName)
            FileOutputStream(photoFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            inputStream?.close()
            photoFile.absolutePath
        } catch (e: Exception) {
            ""
        }
    }

    private fun saveExpense() {
        val name = etName.text.toString()
        val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
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

        btnSubmit.isEnabled = false
        btnSubmit.text = "Saving..."

        Thread {
            try {
                val savedExpense = expenseService.addExpense(
                    name = name,
                    amount = amount,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    photo = selectedPhotoPath,
                    description = description
                )

                runOnUiThread {
                    Toast.makeText(this, "Saved! ID: ${savedExpense.id}", Toast.LENGTH_LONG).show()

                    etName.text.clear()
                    etAmount.text.clear()
                    etDate.text.clear()
                    etStartTime.text.clear()
                    etEndTime.text.clear()
                    etDescription.text.clear()
                    ivPhoto.setImageURI(null)
                    ivPhoto.visibility = android.view.View.GONE
                    selectedPhotoPath = ""
                    currentPhotoBitmap = null

                    btnSubmit.isEnabled = true
                    btnSubmit.text = "Save Expense"
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