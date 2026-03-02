package com.example.chordcraft.components

import android.content.Context
import android.media.SoundPool

// TODO: Make function play Chords Returned by Chord Extraction Model.
fun playbackAudio(context: Context) {
    val soundPool = SoundPool.Builder()
        .setMaxStreams(8)
        .build()

    soundPool.load(context.assets.openFd("soundbank/C4.wav"), 1)
    soundPool.load(context.assets.openFd("soundbank/A4.wav"), 1)
    soundPool.setOnLoadCompleteListener { pool, sampleId, status ->
        if (status == 0) {
            pool.play(sampleId, 1f, 1f, 0, 0, 1f)
        }
    }
}