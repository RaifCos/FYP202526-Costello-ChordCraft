package com.example.chordcraft.components

import android.content.Context
import android.net.Uri

import org.json.JSONObject

fun extractChords(localCall: Boolean, uri: Uri, context: Context): JSONObject {
    val modelOutput: JSONObject
    if (localCall) {
        val tempFile = cacheFileFromURI(context, uri, "audio.wav")
        modelOutput = callPythonReturn("modelCustom", tempFile.absolutePath)
    } else {
        modelOutput = callAPI(context, uri)
    }
    return modelOutput
}

fun getChords(localCall: Boolean, uri: Uri, context: Context): String {
    return extractChords(localCall, uri, context).toString()
}