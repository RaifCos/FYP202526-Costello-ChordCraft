package com.example.chordcraft.components

import android.content.Context
import android.net.Uri

import org.json.JSONObject

fun extractChords(localCall: Boolean, uri: Uri, context: Context): JSONObject {
    val modelOutput: JSONObject
    if (localCall) {
        val tempFile = cacheFileFromURI(context, uri, "audio.wav")
        modelOutput = callPythonJSON("modelCustom", "main", tempFile.absolutePath)
    } else {
        modelOutput = callAPI(context, uri)
    }

    // Get MIDI values from chordProcessing.py.
    val chordJSON = callPythonJSON("chordProcessing", "getChordTemplates", modelOutput.toString())

    return chordJSON
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