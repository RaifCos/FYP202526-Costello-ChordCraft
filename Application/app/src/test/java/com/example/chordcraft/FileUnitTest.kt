package com.example.chordcraft

import android.content.ContentResolver
import android.content.Context
import com.example.chordcraft.components.readWAV
import com.example.chordcraft.components.writeWav
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.mock
import java.io.File

class FileUnitTest {

    @Test
    fun readWAV_EmptyBytes() {
        assertEquals(readWAV(byteArrayOf()), null)
    }

    @Test
    fun readWAV_InvalidHeader() {
        val notWav = byteArrayOf(0x00, 0x01, 0x02, 0x03)
        assertEquals(readWAV(notWav), null)
    }

    @Test
    fun writeWav_NullFile() {
        val mockFile = mock(File::class.java)
        assertThrows(Exception::class.java) {
            writeWav(shortArrayOf(), mockFile)
        }
    }
}