package com.example.chordcraft.components

import android.content.Context
import android.net.Uri
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.time.Duration.Companion.seconds
import org.json.JSONObject

fun callAPI(context: Context, uri: Uri): JSONObject {
    val client = OkHttpClient.Builder()
        .connectTimeout(30.seconds)
        .readTimeout(150.seconds)
        .writeTimeout(60.seconds)
        .build()


    val stream = context.contentResolver.openInputStream(uri)
        ?: throw Exception("Couldn't open file.")

    val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: throw Exception("Couldn't resolve filename.")

    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

    val bytes = stream.use { it.readBytes() }

    if (bytes.isEmpty()) {
        throw Exception("File cannot be read or is empty.")
    }

    // Form Request Body from Audio File.
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            "file",
            fileName,
            bytes.toRequestBody(mimeType.toMediaType())
        )
        .build()

    // Build Request from URL and Request Body.
    val request = Request.Builder()
        .url("https://fyp202526-costello-chordcraft-backend-production.up.railway.app/run")
        .post(requestBody)
        .build()

    // Catch any OkHttp Errors.
    val response = try {
        client.newCall(request).execute()
    } catch (e: java.net.UnknownHostException) {
        throw Exception("Unable to connect to the server.\nPlease check your connection and try again.")
    } catch (e: java.net.SocketTimeoutException) {
        throw Exception("The request timed out.\nPlease try again shortly.")
    } catch (e: java.io.IOException) {
        throw Exception("A network error occurred: ${e.message}\nPlease check your connection and try again.")
    }

    // Process API Response
    response.use {
        val body = it.body.string()

        if (!it.isSuccessful) {
            val detail = runCatching {
                val json = JSONObject(body)
                json.optString("detail").ifBlank { null }
                    ?: json.optString("message").ifBlank { null }
                    ?: body
            }.getOrDefault(body.ifBlank { "Unknown error" })
            throw Exception("The Chord Extraction API encountered an error.\nTry again shortly, or use an alternative model.\nError Code ${it.code}: $detail")
        }

        val processedResult = body.trim().let { b ->
            if (b.startsWith("\"") && b.endsWith("\""))
                b.substring(1, b.length - 1).replace("\\\"", "\"").replace("\\\\", "\\")
            else b
        }
        return JSONObject(processedResult)
    }
}