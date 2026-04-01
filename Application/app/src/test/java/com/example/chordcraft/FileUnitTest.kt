package com.example.chordcraft

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.chordcraft.components.cacheFileFromURI
import com.example.chordcraft.components.readWAV
import com.example.chordcraft.components.writeWav
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.File

class FileUnitTest {

    private lateinit var mockContext: Context
    private lateinit var mockContentResolver: ContentResolver

    @Test
    fun readWAV_EmptyBytes() {
        assertEquals(readWAV(byteArrayOf()), null)
    }

    @Test
    fun writeWav_NullFile() {
        val mockFile = mock(File::class.java)
        assertThrows(Exception::class.java) {
            writeWav(shortArrayOf(), mockFile)
        }
    }
}