package com.example.chordcraft

import android.content.ContentResolver
import android.content.Context
import com.example.chordcraft.components.playbackChords
import org.junit.Test
import org.mockito.Mockito.mock

class PlaybackUnitTest {

    private lateinit var mockContext: Context
    private lateinit var mockContentResolver: ContentResolver

    @Test
    fun playbackChords_NoChords() {
        mockContext = mock(Context::class.java)
        playbackChords(mockContext, emptyList())
    }
}