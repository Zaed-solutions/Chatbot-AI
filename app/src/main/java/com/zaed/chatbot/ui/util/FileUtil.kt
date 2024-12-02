package com.zaed.chatbot.ui.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.aallam.openai.api.image.ImageURL
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.zaed.chatbot.data.model.FileType
import com.zaed.chatbot.data.model.MessageAttachment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
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
fun convertJsonToJsonl(jsonString: String): String {
    val jsonArray = JSONArray(jsonString)
    val stringBuilder = StringBuilder()
    for (i in 0 until jsonArray.length()) {
        stringBuilder.append(jsonArray.getJSONObject(i).toString())
        stringBuilder.append("\n")
    }
    return stringBuilder.toString()
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
fun readPdfContent(context: Context, fileUri: Uri): String {
    var stringResult = ""
    PDFBoxResourceLoader.init(context);
    context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
        PDDocument.load(inputStream).use { pdfDocument ->
            if (!pdfDocument.isEncrypted) {
                stringResult = PDFTextStripper().getText(pdfDocument)
            }
        }
    }

    return stringResult
}
fun readExcelContent(context: Context, fileUri: Uri): String {

    return ""
}

fun readWordContent(context: Context, fileUri: Uri): String {
    return ""
}

fun convertToJsonl(sourceFile: File): File {
    // Read the original file content
    val originalContent = sourceFile.readText()

    // Assuming the original content is a JSON array, convert it to JSONL
    val jsonArray = org.json.JSONArray(originalContent) // Requires org.json library
    val jsonlString = StringBuilder()
    for (i in 0 until jsonArray.length()) {
        jsonlString.append(jsonArray.getJSONObject(i).toString())
        jsonlString.append("\n")
    }

    // Write the JSONL string to a new file
    val jsonlFile = File(sourceFile.parent, "${sourceFile.nameWithoutExtension}.jsonl")
    jsonlFile.writeText(jsonlString.toString())
    return jsonlFile
}

fun contentUriToByteArray(context: Context, uri: Uri): ByteArray? {
    return try {
        // Open an InputStream for the content URI
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            // Read the InputStream into a ByteArrayOutputStream
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (stream.read(buffer).also { bytesRead = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }
            byteArrayOutputStream.toByteArray() // Convert to ByteArray
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null // Return null in case of an error
    }
}
private fun allowedMimeTypes(): List<String> {
    return listOf(
        "application/pdf", // PDF
        "application/vnd.ms-excel", // Excel .xls
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // Excel .xlsx
        "application/msword", // Word .doc
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // Word .docx
    )
}
fun Uri.toFileUri(context: Context): Uri? {
    return try {
        if ("file".equals(this.scheme, ignoreCase = true)) {
            // Already a file URI
            return this
        } else if ("content".equals(this.scheme, ignoreCase = true)) {
            // Handle content:// URI
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            context.contentResolver.query(this, projection, null, null, null)?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                if (cursor.moveToFirst()) {
                    val filePath = cursor.getString(columnIndex)
                    if (!filePath.isNullOrEmpty()) {
                        return Uri.fromFile(File(filePath))
                    }
                }
            }
            // For Android Q and above or when DATA column is not available
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val file = File(context.cacheDir, "tempFile_${System.currentTimeMillis()}")
                context.contentResolver.openInputStream(this)?.use { inputStream ->
                    copyStreamToFile(inputStream, file)
                }
                return Uri.fromFile(file)
            }
        }
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun convertJsonToJsonl(jsonFile: File, jsonlFile: File) {
    val jsonArray = JSONArray(jsonFile.readText()) // Read JSON Array from the file
    jsonlFile.bufferedWriter().use { writer ->
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            writer.write(jsonObject.toString())
            writer.newLine()
        }
    }
}
private fun copyStreamToFile(inputStream: InputStream, file: File) {
    FileOutputStream(file).use { outputStream ->
        inputStream.copyTo(outputStream)
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

fun getMimeType(context: Context, uri: Uri): String {
    return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        context.contentResolver.getType(uri)
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
    }?: "application/octet-stream"
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
suspend fun downloadImageWithMediaStore(context: Context, imageUrl: String) {
    val client = OkHttpClient()

    // Make network call to download the image
    val request = Request.Builder().url(imageUrl).build()
    val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }

    if (response.isSuccessful) {
        val imageBytes = response.body?.byteStream()
        val fileName = "downloaded_image_${System.currentTimeMillis()}.jpg"

        // Save image using MediaStore for Android 10 and higher
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            withContext(Dispatchers.IO) {
                resolver.openOutputStream(uri)?.use { outputStream: OutputStream ->
                    imageBytes?.copyTo(outputStream)
                }
            }
            Toast.makeText(context, "Image saved to Downloads", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
    }
}
suspend fun downloadImage(context: Context, imageUrl: String) {
    val client = OkHttpClient()

    // Make network call to download the image
    val request = Request.Builder().url(imageUrl).build()
    val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }

    if (response.isSuccessful) {
        val imageBytes = response.body?.byteStream()
        val fileName = "downloaded_image_${System.currentTimeMillis()}.jpg"
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        // Write to file
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { output ->
                imageBytes?.copyTo(output)
            }
        }

        Toast.makeText(context, "Image saved to Downloads", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
    }
}