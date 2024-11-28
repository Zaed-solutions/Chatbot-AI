package com.zaed.chatbot.ui.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.aallam.openai.api.image.ImageURL
import com.zaed.chatbot.data.model.FileType
import com.zaed.chatbot.data.model.MessageAttachment
import java.io.File
import java.io.InputStream
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

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    val newUri = Uri.parse(uri.toString())
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(newUri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun createTempFileFromUri(uri: Uri, fileName: String, context: Context): File? {
    return try {
        val tempFile = File(context.cacheDir, fileName)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getMimeType(context: Context, uri: Uri): String? {
    return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        context.contentResolver.getType(uri)
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
    }
}

fun List<ImageURL>.toMessageAttachments(): List<MessageAttachment> {
    return this.map { it.toMessageAttachment() }
}
fun ImageURL.toMessageAttachment(): MessageAttachment =
    MessageAttachment(
        name = revisedPrompt?:"",
        FileType.IMAGE,
        uri = url.toUri()
    )