package com.example.chordcraft.components

import android.content.Context
import android.net.Uri
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

fun callAPI(context: Context, uri: Uri): JSONObject {
    val client = OkHttpClient()
    val stream = context.contentResolver.openInputStream(uri) ?: return JSONObject().put("Error", "Couldn't open file.")

    val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: return JSONObject().put("Error", "Couldn't resolve filename.")

    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

    // Form Request Body from Audio File
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", fileName, stream.readBytes().toRequestBody(mimeType.toMediaType()))
        .build()

    // Build Request from URL and Request Body.
    val request = Request.Builder()
        .url("https://fyp202526-costello-chordcraft-backend-production.up.railway.app/run")
        .post(requestBody)
        .build()

    // Call API.
    val response = client.newCall(request).execute().use { it.body.string() }

    // Process result into a format that can be converted into JSON.
    val processedResult = response.trim().let {
        if (it.startsWith("\"") && it.endsWith("\""))
            it.substring(1, it.length - 1).replace("\\\"", "\"").replace("\\\\", "\\")
        else it
    }

    return JSONObject(processedResult)
}