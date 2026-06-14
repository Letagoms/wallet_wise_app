// utils/PhotoHelper.kt
package com.example.wallet_wise_app.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PhotoHelper {

    fun createPhotoFile(context: Context): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            File.createTempFile(fileName, ".jpg", storageDir)
        } catch (e: Exception) {
            null
        }
    }

    fun getPhotoUri(context: Context, photoFile: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
        } catch (e: Exception) {
            null
        }
    }

    fun savePhotoToStorage(context: Context, photoUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(photoUri)
            val fileName = "${UUID.randomUUID()}.jpg"
            val appDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val photoFile = File(appDirectory, fileName)
            FileOutputStream(photoFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            inputStream?.close()
            photoFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun loadBitmapFromPath(path: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            null
        }
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: Exception) {
            null
        }
    }

    fun deletePhoto(photoPath: String?) {
        if (!photoPath.isNullOrEmpty()) {
            val file = File(photoPath)
            if (file.exists()) file.delete()
        }
    }
}