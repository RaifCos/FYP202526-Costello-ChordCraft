package com.example.chordcraft.components

import android.content.Context
import android.media.MediaPlayer
import java.io.File
import kotlin.math.roundToInt

// Define Audio Format Constants.
private const val SAMPLE_RATE = 48000
private const val CHANNELS = 2

// Function to analyze provided Chordal data, and use it to render and play an Audio output.
fun playbackChords(context: Context, chordList: List<Chord>) {

    if (chordList.isEmpty()) { return }

    // Render Audio File and play.
    val chordAudioFile = File(context.cacheDir, "chordAudio.wav")
    renderChordAudio(context, chordList, chordAudioFile)
    val mediaPlayer = MediaPlayer().apply {
        setDataSource(chordAudioFile.absolutePath)
        prepare()
        start()
    }
}

// Function that generates an audio file from a JSON of MIDI numbers and chord times.
fun renderChordAudio(context: Context, chordList: List<Chord>, outputFile: File) {
    // Find the end time of the final chord to get the total length.
    val totalSeconds = 2.0 + chordList.last().endTime

    val totalSamples = (totalSeconds * SAMPLE_RATE).roundToInt()
    val mixBuffer = FloatArray(totalSamples * CHANNELS)

    // Load each Chord.
    for (chord in chordList) {
        val sample = (chord.startTime * SAMPLE_RATE).roundToInt() * CHANNELS
        val chordNotes = chord.notes

        // Load each Note.
        for (j in 0 until chordNotes.size) {
            val midiNote = chordNotes[j]
            val samples = loadSoundbankWAV(context, midiNote) ?: continue

            // Mix Soundbank Samples into the buffer.
            val end = minOf(sample + samples.size, mixBuffer.size)
            for (k in sample until end) {
                mixBuffer[k] += samples[k - sample]
            }
        }
    }

    // Normalize to prevent Clipping.
    val peak = mixBuffer.maxOrNull()?.let { maxOf(it, -mixBuffer.minOrNull()!!) } ?: 1f
    val scale = if (peak > 1f) 1f / peak else 1f

    // Convert values into 16-bit PCM to store in the WAV file.
    val pcmData = ShortArray(mixBuffer.size) { idx ->
        (mixBuffer[idx] * scale * Short.MAX_VALUE)
            .coerceIn(Short.MIN_VALUE.toFloat(), Short.MAX_VALUE.toFloat())
            .toInt().toShort()
    }

    // Write Data to output file.
    writeWav(pcmData, outputFile)
}
// Function to load a note WAV from the Soundbank.
private fun loadSoundbankWAV(context: Context, midiNote: Int): FloatArray? {
    val assetPath = "soundbank/MIDI_$midiNote.wav"
    try {
        context.assets.open(assetPath).use { inputStream ->
            val bytes = inputStream.readBytes()
            return readWAV(bytes)
        }
    } catch (e: Exception) {
        android.util.Log.w("ChordAudioRenderer", "$assetPath not found.")
        return null
    }
}