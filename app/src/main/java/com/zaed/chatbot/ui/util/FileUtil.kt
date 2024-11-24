package com.zaed.chatbot.ui.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    val contentResolver = context.contentResolver
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && it.moveToFirst()) {
            return it.getString(nameIndex)
        }
    }
    return null
}
fun createImageFile(context: Context): Uri {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val file = File.createTempFile("photo_${timestamp}_", ".jpg", storageDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
