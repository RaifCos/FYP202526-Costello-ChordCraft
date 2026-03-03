package com.example.chordcraft.components

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext

import java.io.File

@Composable
fun filePickerLauncher(selectedFileUri: MutableState<Uri?>): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        // Check the File exists and is the correct format.
            uri: Uri? ->
        if (uri != null && formatValidation(context, uri)) {
            selectedFileUri.value = uri
            Toast.makeText(context, "File selected: $uri", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error Selecting File. Make sure it is .MP3 or .WAV", Toast.LENGTH_LONG).show()
        }
    } // Return a lambda callable from the Main Menu.
    return { launcher.launch("audio/*") }
}

fun formatValidation(context: Context, uri: Uri): Boolean {

    // Check File Type to confirm user uploaded a MP3 or WAV file.
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri)
    val isValidMime = mimeType == "audio/mpeg" ||  // .mp3
            mimeType == "audio/wav"   ||  // some devices
            mimeType == "audio/x-wav"

    // Check File Extension to Confirm.
    val extension = MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(mimeType)
        ?: uri.toString().substringAfterLast('.', "").lowercase()
    val isValidExtension = extension == "mp3" || extension == "wav"

    return isValidMime || isValidExtension
}

@Composable
fun getFileName(uri: Uri): String {
    val context = LocalContext.current
    var result = ""
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) { result = it.getString(nameIndex) }
            }
        }
    }
    return result
}

fun cacheFileFromURI(context: Context, uri: Uri, name: String): File {
    val tempFile = File(context.cacheDir, name)
    context.contentResolver.openInputStream(uri)?.use { input ->
        tempFile.outputStream().use { output -> input.copyTo(output) }
    }
    return tempFile
}