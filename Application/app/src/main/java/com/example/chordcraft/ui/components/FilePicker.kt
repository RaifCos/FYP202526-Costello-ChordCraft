package com.example.chordcraft.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext

@Composable
fun filePickerLauncher(selectedFileUri: MutableState<Uri?>): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri: Uri? ->
        if (uri != null) {
            selectedFileUri.value = uri
            Toast.makeText(context, "File selected: $uri", Toast.LENGTH_SHORT).show()
        }
    } // Return a lambda callable from the MainMenu Activity. 
    return { launcher.launch("audio/*") }
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
                if (nameIndex >= 0) {
                    result = it.getString(nameIndex)
                }
            }
        }
    }
    return result
}
