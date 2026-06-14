// utils/PhotoHelper.kt
// UTILITY CLASS - Helper functions that don't fit anywhere else
// All functions are "pure" - they just do one specific task and don't manage state
// OBJECT = Singleton - only one instance exists, all functions are like static methods

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

    // ========== CREATE TEMPORARY FILE FOR CAMERA ==========
    // Creates a unique temp file to store a photo taken by the camera
    // Returns a File object (or null if failed)
    // Called BEFORE opening the camera so we know where to save the photo
    fun createPhotoFile(context: Context): File? {
        // Create timestamp for unique filename (e.g., "20240614_153045")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // Filename pattern: JPEG_20240614_153045_
        val fileName = "JPEG_${timeStamp}_"

        // Get the app's private pictures directory (not accessible by gallery)
        // This directory is automatically deleted when app is uninstalled
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return try {
            // Create actual temp file with .jpg extension
            File.createTempFile(fileName, ".jpg", storageDir)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ========== GET URI FOR CAMERA PHOTO (FileProvider) ==========
    // Converts a File into a URI that the Camera app can write to
    // Required for Android 7.0+ (cannot share file:// URIs directly)
    // Uses FileProvider for security
    fun getPhotoUri(context: Context, photoFile: File): Uri? {
        return try {
            // FileProvider creates a content:// URI that other apps can access
            // "${context.packageName}.fileprovider" must match AndroidManifest.xml
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",  // Matches authorities in manifest
                photoFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ========== SAVE PHOTO FROM GALLERY/CAMERA TO APP STORAGE ==========
    // Takes a content:// URI (from gallery picker or camera)
    // Copies the photo to app's private storage and returns file path
    // Returns null if failed
    fun savePhotoToStorage(context: Context, photoUri: Uri): String? {
        return try {
            // Open input stream to read the selected photo
            val inputStream = context.contentResolver.openInputStream(photoUri)

            // Generate unique filename using random UUID (e.g., "a3f4b5c6-...jpg")
            val fileName = "${UUID.randomUUID()}.jpg"

            // Get app's private pictures directory
            val appDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val photoFile = File(appDirectory, fileName)

            // Copy the photo from input stream to our app's file
            FileOutputStream(photoFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            // Close the input stream
            inputStream?.close()

            // Return the absolute file path (e.g., "/storage/emulated/0/Android/data/.../photo.jpg")
            photoFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ========== LOAD BITMAP FROM FILE PATH ==========
    // Converts a file path string into a Bitmap (image) that can be displayed in ImageView
    // Used when we already have the file path saved in database
    fun loadBitmapFromPath(path: String): Bitmap? {
        return try {
            // Decode the file into a Bitmap object
            BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ========== LOAD BITMAP FROM URI ==========
    // Converts a content:// URI into a Bitmap (image)
    // Used immediately after selecting a photo from gallery
    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            // MediaStore helper decodes the URI into a Bitmap
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ========== DELETE PHOTO FILE ==========
    // Deletes the physical photo file from storage
    // Should be called when expense is deleted to clean up storage
    // Checks if file exists before trying to delete
    fun deletePhoto(photoPath: String?) {
        // Only proceed if path is not null or empty
        if (!photoPath.isNullOrEmpty()) {
            val file = File(photoPath)
            // Check if file exists before deleting (avoid FileNotFoundException)
            if (file.exists()) {
                file.delete()  // Returns true if successful, false if failed
            }
        }
    }
}