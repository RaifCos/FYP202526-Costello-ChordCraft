package com.example.chordcraft.components

import android.content.Context
import android.media.SoundPool

// TODO: Make function play Chords Returned by Chord Extraction Model.
fun playbackAudio(context: Context) {
    val soundPool = SoundPool.Builder()
        .setMaxStreams(8)
        .build()

    val soundFiles = listOf(
        "soundbank/00_C4.wav",
        "soundbank/04_E4.wav",
        "soundbank/07_G4.wav"
    )

    val sampleIds = mutableListOf<Int>()
    var loadedCount = 0

    soundFiles.forEach { file ->
        val sampleId = soundPool.load(context.assets.openFd(file), 1)
        sampleIds.add(sampleId)
    }

    soundPool.setOnLoadCompleteListener { pool, _, status ->
        if (status == 0) {
            loadedCount++
            if (loadedCount == soundFiles.size) {
                sampleIds.forEach { id ->
                    pool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }
}