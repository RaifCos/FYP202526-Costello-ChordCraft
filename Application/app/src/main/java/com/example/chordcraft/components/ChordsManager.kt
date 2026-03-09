package com.example.chordcraft.components

import android.content.Context
import android.net.Uri

import org.json.JSONObject

data class Chord(
    val label: String,
    val notes: List<Int>,
    val startTime: Double,
    val endTime: Double
)

fun extractChords(localCall: Boolean, uri: Uri, context: Context): List<Chord> {
    val modelOutput: JSONObject
    if (localCall) {
        val tempFile = cacheFileFromURI(context, uri, "audio.wav")
        modelOutput = callPythonJSON("modelCustom", "main", tempFile.absolutePath)
    } else {
        modelOutput = callAPI(context, uri)
    }

    // Get MIDI values from chordProcessing.py.
    val chordJSON = callPythonJSON("chordProcessing", "getChordTemplates", modelOutput.toString())
    val chordsArray = chordJSON.getJSONArray("chords")

    val chordList = mutableListOf<Chord>()
    for (i in 0 until chordsArray.length()) {
        val chord = chordsArray.getJSONObject(i)
        chordList.add(
            Chord(
                label = chord.getString("chord"),
                // Cast JSONArray to List<Int>
                notes = chord.getJSONArray("intervals").let
                { arr ->  (0 until arr.length()).map { arr.getInt(it) } },
                startTime = chord.getDouble("start"),
                endTime = chord.getDouble("end")
            )
        )
    }

    return chordList
}

fun generateChordString(chordList: List<Chord>): String {
    return chordList.joinToString(", ") { it.label }
}