package com.example.chordcraft.components

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import androidx.annotation.RequiresPermission

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File

private const val SAMPLE_RATE = 48000
@RequiresPermission(Manifest.permission.RECORD_AUDIO)
suspend fun liveRecordingHandler(context: Context, viewModel: ChordViewModel) {
    val channelConfig = AudioFormat.CHANNEL_IN_STEREO
    val audioFormat   = AudioFormat.ENCODING_PCM_16BIT
    val chunkSeconds  = 2

    val minBuffer  = AudioRecord.getMinBufferSize(SAMPLE_RATE, channelConfig, audioFormat)
    val bufferSize = minBuffer.coerceAtLeast(SAMPLE_RATE * 2 * chunkSeconds * 2)

    // Start Recording
    val recorder = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        SAMPLE_RATE,
        channelConfig,
        audioFormat,
        bufferSize
    )

    recorder.startRecording()

    try {
        val samplesPerChunk = SAMPLE_RATE * 2 * chunkSeconds
        val pcmBuffer = ShortArray(samplesPerChunk)

        // Load the last two seconds of Audio from Recording.
        while (currentCoroutineContext().isActive) {
            var totalRead = 0
            while (totalRead < samplesPerChunk && currentCoroutineContext().isActive) {
                val read = recorder.read(pcmBuffer, totalRead, samplesPerChunk - totalRead)
                if (read <= 0) break
                totalRead += read
            }
            if (!currentCoroutineContext().isActive) break

            // Write Recording Data to WAV file.
            val tempWav = File(context.cacheDir, "live_chunk.wav")
            writeWav(pcmBuffer, tempWav)  // from ChordAudioRenderer.kt

            // Run ACR Model.
            val chords = extractChords(true, uri = Uri.fromFile(tempWav), context)
            withContext(Dispatchers.Main) {
                viewModel.chordList.value = chords
            }
        }
    } finally {
        recorder.stop()
        recorder.release()
    }
}