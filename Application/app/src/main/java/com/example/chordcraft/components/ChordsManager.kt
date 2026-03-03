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

fun generateChordString(modelOutput: JSONObject): String {
    val result = StringBuilder()
    val chordsArray = modelOutput.getJSONArray("chords")

    // Append each Chord Label to the result String.
    for (i in 0 until chordsArray.length()) {
        val chord = chordsArray.getJSONObject(i)
        val label = chord.getString("chord")
        result.append(label)
        if (i < chordsArray.length() - 1) {
            result.append(", ")
        }
    }
    return result.toString()
}

fun getChords(localCall: Boolean, uri: Uri, context: Context): String {
    val modelOutput = extractChords(localCall, uri, context)
    return generateChordString(modelOutput)
}