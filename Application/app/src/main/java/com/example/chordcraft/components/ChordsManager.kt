package com.example.chordcraft.components

import android.content.Context
import android.net.Uri

import org.json.JSONObject

fun ExtractChords(localCall: Boolean, uri: Uri, context: Context) {
    val modelOutput: JSONObject
    if (localCall) {
        val tempFile = cacheFileFromURI(context, uri, "audio.wav")
        modelOutput = callPythonReturn("modelCustom", tempFile.absolutePath)
    } else {
        modelOutput = callAPI(context, uri)
    }
}

fun GetChords(localCall: Boolean, uri: Uri, context: Context): String {
    return ExtractChords(localCall, uri, context).toString()
}