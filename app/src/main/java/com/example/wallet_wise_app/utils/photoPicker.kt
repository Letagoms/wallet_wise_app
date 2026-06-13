// utils/PhotoHelper.kt
package com.example.wallet_wise_app.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object PhotoHelper {

    // Save photo from Uri to app storage and return file path
    fun savePhotoToStorage(context: Context, photoUri: Uri): String? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(photoUri)

            // Create unique filename
            val fileName = "${UUID.randomUUID()}.jpg"

            // Get app's private storage directory
            val appDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val photoFile = File(appDirectory, fileName)

            // Save file
            FileOutputStream(photoFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            inputStream?.close()
            photoFile.absolutePath // Return the file path
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Delete photo when expense is deleted
    fun deletePhoto(photoPath: String?) {
        if (!photoPath.isNullOrEmpty()) {
            val file = File(photoPath)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}