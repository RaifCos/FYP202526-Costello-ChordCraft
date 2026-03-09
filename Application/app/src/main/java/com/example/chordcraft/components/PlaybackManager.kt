package com.example.chordcraft.components

import android.content.Context
import android.media.MediaPlayer

import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

import kotlin.math.roundToInt

// Define Audio Format Constants.
private const val SAMPLE_RATE = 48000
private const val BIT_DEPTH = 16
private const val CHANNELS = 2

// Function to analyze provided Chordal data, and use it to render and play an Audio output.
fun playbackChords(context: Context, chordList: List<Chord>) {
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

// Function to parse WAV data and convert to Float for sample mixing.
private fun readWAV(bytes: ByteArray): FloatArray? {
    val buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

    // Check format header to confirm the file is WAV.
    val riff = String(bytes, 0, 4)
    if (riff != "RIFF") return null
    buf.position(8)
    val wave = String(bytes, 8, 4)
    if (wave != "WAVE") return null

    // Prepare to read Audio specs ("fmt") Chunk.
    var audioFormat = 0;
    var numChannels = 0
    var sampleRate = 0;
    var bitsPerSample = 0

    // Prepare to read Data Chunk.
    var dataOffset = 0;
    var dataSize = 0

    buf.position(12)
    while (buf.remaining() > 8) {
        val chunkId = String(bytes, buf.position(), 4)
        buf.position(buf.position() + 4)
        val chunkSize = buf.int

        // Read Data of each Chunk.
        when (chunkId) {
            "fmt " -> {
                audioFormat  = buf.short.toInt() and 0xFFFF
                numChannels  = buf.short.toInt() and 0xFFFF
                sampleRate   = buf.int
                buf.int  // Byte Rate / Bytes per Second.
                buf.short // Block Align / Bytes per Frame.
                bitsPerSample = buf.short.toInt() and 0xFFFF
                if (chunkSize > 16) buf.position(buf.position() + chunkSize - 16)
            }
            "data" -> {
                dataOffset = buf.position()
                dataSize   = chunkSize
                break
            }
            else -> buf.position(buf.position() + chunkSize)
        }
    }

    // Convert Samples to Float.
    val frameCount = dataSize / (numChannels * 2)
    val samples = FloatArray(frameCount * numChannels)
    buf.position(dataOffset)

    // Normalize Samples for mixer.
    for (i in 0 until frameCount * numChannels) {
        samples[i] = buf.short.toFloat() / Short.MAX_VALUE
    }
    return samples
}

// Function to write a 16-bit PCM WAV file.
private fun writeWav(pcm: ShortArray, file: File) {
    val dataSize = (pcm.size * 2)

    // Specify Bytes per Second and Frame.
    val bytesPerSec = SAMPLE_RATE * CHANNELS * BIT_DEPTH / 8
    val bytesPerFrame = CHANNELS * BIT_DEPTH / 8

    FileOutputStream(file).use { fos ->
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)

        // Specify RIFF format.
        header.put("RIFF".toByteArray())
        header.putInt(36 + dataSize)
        header.put("WAVE".toByteArray())

        // Specify Audio specs.
        header.put("fmt ".toByteArray())
        header.putInt(16)
        header.putShort(1)
        header.putShort(CHANNELS.toShort())
        header.putInt(SAMPLE_RATE)
        header.putInt(bytesPerSec)
        header.putShort(bytesPerFrame.toShort())
        header.putShort(BIT_DEPTH.toShort())

        // Write Audio data.
        header.put("data".toByteArray())
        header.putInt(dataSize)

        // Write headers to WAV File.
        fos.write(header.array())
        val sampleBuf = ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN)
        pcm.forEach { sampleBuf.putShort(it) }
        fos.write(sampleBuf.array())
    }
}