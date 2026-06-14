// utils/ReceiptScanner.kt
// UTILITY CLASS that uses Google ML Kit to extract text from receipt photos
// This performs Optical Character Recognition (OCR) - converting images to text
// It then uses REGEX (regular expressions) to find specific patterns like amounts and dates
// Helps users auto-fill expense forms instead of typing everything manually

package com.example.wallet_wise_app.utils

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.regex.Pattern

// OBJECT = Singleton - only one instance exists in the entire app
object ReceiptScanner {

    // ========== ML KIT TEXT RECOGNIZER ==========
    // This is the Google ML Kit client that does the actual text detection
    // It's configured for Latin alphabet text (English, Spanish, French, etc.)
    // Takes a photo and returns all the text it finds
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // ========== DATA CLASS FOR SCANNED RESULTS ==========
    // Holds the extracted information from the receipt
    // Only 3 fields are extracted (can be extended later)
    data class ScannedData(
        val amount: String,    // Extracted price (e.g., "49.99")
        val date: String,      // Extracted date (e.g., "2026-06-14")
        val name: String       // Extracted store/restaurant name (e.g., "Starbucks")
    )

    // ========== MAIN SCANNING FUNCTION ==========
    // Takes a Bitmap (photo) and a callback function
    // Callback is called when scanning completes (success or failure)
    // Scanning happens asynchronously (doesn't block the UI)
    fun scanReceipt(bitmap: Bitmap, callback: (ScannedData) -> Unit) {
        // Convert Android Bitmap to ML Kit's InputImage format
        val image = InputImage.fromBitmap(bitmap, 0)

        // ========== START TEXT RECOGNITION ==========
        // This runs asynchronously - results come back later via listeners
        recognizer.process(image)
            // ========== SUCCESS: Text found ==========
            .addOnSuccessListener { visionText ->
                // Get ALL text from the image as one String
                // This includes everything on the receipt: store name, items, prices, dates, total, etc.
                val fullText = visionText.text

                // Example of what fullText might look like:
                // "Starbucks Coffee\n2026-06-14\nLatte     $4.50\nCappuccino $5.25\nTotal: $9.75"

                // ========== EXTRACT AMOUNT using REGEX ==========
                // REGEX Pattern: [R]?\\s*(\\d+\\.\\d{2})
                // This looks for:
                //   [R]?     - Optional R symbol (for South African Rand)
                //   \\s*     - Optional whitespace
                //   (\\d+\\.\\d{2}) - Captures: one or more digits, a dot, then exactly 2 digits (e.g., 49.99)
                val amountPattern = Pattern.compile("[R]?\\s*(\\d+\\.\\d{2})")
                val amountMatcher = amountPattern.matcher(fullText)
                var amount = ""
                if (amountMatcher.find()) {
                    // group(1) gets the captured number without the R symbol
                    amount = amountMatcher.group(1)  // "49.99" not "R49.99"
                }

                // ========== EXTRACT DATE using REGEX ==========
                // REGEX Pattern: (\\d{4}-\\d{2}-\\d{2})|(\\d{2}/\\d{2}/\\d{4})
                // This looks for:
                //   \\d{4}-\\d{2}-\\d{2} - YYYY-MM-DD format (e.g., 2026-06-14)
                //   OR
                //   \\d{2}/\\d{2}/\\d{4} - DD/MM/YYYY format (e.g., 14/06/2026)
                val datePattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})|(\\d{2}/\\d{2}/\\d{4})")
                val dateMatcher = datePattern.matcher(fullText)
                var date = ""
                if (dateMatcher.find()) {
                    date = dateMatcher.group()  // "2026-06-14" or "14/06/2026"
                }

                // ========== EXTRACT STORE NAME ==========
                // Split the full text into individual lines
                val lines = fullText.split("\n")
                var name = ""

                // Look for lines containing common store keywords
                for (line in lines) {
                    val lowerLine = line.lowercase()  // Convert to lowercase for case-insensitive search
                    if (lowerLine.contains("store") ||
                        lowerLine.contains("market") ||
                        lowerLine.contains("cafe") ||
                        lowerLine.contains("shop")) {
                        name = line  // Found a likely store name
                        break  // Stop searching
                    }
                }

                // If no keyword found, just take the first line (up to 30 characters)
                if (name.isBlank() && lines.isNotEmpty()) {
                    name = lines[0].take(30)  // First line, max 30 chars
                }

                // ========== RETURN RESULTS VIA CALLBACK ==========
                // Pass the extracted data back to the calling function
                callback(ScannedData(amount, date, name))
            }
            // ========== FAILURE: Error occurred ==========
            .addOnFailureListener { e ->
                // Return empty data on failure
                // The UI will show "No data found" message
                callback(ScannedData("", "", ""))
            }
    }
}