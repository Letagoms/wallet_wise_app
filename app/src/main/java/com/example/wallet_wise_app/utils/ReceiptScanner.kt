// utils/ReceiptScanner.kt
package com.example.wallet_wise_app.utils

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.regex.Pattern

object ReceiptScanner {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    data class ScannedData(
        val amount: String,
        val date: String,
        val name: String
    )

    fun scanReceipt(bitmap: Bitmap, callback: (ScannedData) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val fullText = visionText.text

                // Extract amount (looks for R followed by numbers or just numbers with decimal)
                val amountPattern = Pattern.compile("[R]?\\s*(\\d+\\.\\d{2})")
                val amountMatcher = amountPattern.matcher(fullText)
                var amount = ""
                if (amountMatcher.find()) {
                    amount = amountMatcher.group(1)
                }

                // Extract date (looks for YYYY-MM-DD or DD/MM/YYYY)
                val datePattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})|(\\d{2}/\\d{2}/\\d{4})")
                val dateMatcher = datePattern.matcher(fullText)
                var date = ""
                if (dateMatcher.find()) {
                    date = dateMatcher.group()
                }

                // Extract store name (first line or line with "Store", "Market", "Cafe")
                val lines = fullText.split("\n")
                var name = ""
                for (line in lines) {
                    val lowerLine = line.lowercase()
                    if (lowerLine.contains("store") ||
                        lowerLine.contains("market") ||
                        lowerLine.contains("cafe") ||
                        lowerLine.contains("shop")) {
                        name = line
                        break
                    }
                }
                if (name.isBlank() && lines.isNotEmpty()) {
                    name = lines[0].take(30)
                }

                callback(ScannedData(amount, date, name))
            }
            .addOnFailureListener { e ->
                callback(ScannedData("", "", ""))
            }
    }
}