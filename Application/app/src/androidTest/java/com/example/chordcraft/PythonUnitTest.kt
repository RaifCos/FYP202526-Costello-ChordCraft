package com.example.chordcraft

import android.content.ContentResolver
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chordcraft.components.callPythonJSON
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PythonUnitTest {

    private lateinit var mockContext: Context
    private lateinit var mockContentResolver: ContentResolver

    @Test
    fun callPythonJSON_InvalidModule() {
        assertThrows(Exception::class.java) {
            callPythonJSON("", "main", "misc")
        }
    }

    @Test
    fun callPythonJSON_InvalidAttribute() {
        assertThrows(Exception::class.java) {
            callPythonJSON("modelCustom", "", "")
        }
    }

    @Test
    fun callPythonJSON_InvalidParameter() {
        assertThrows(Exception::class.java) {
            callPythonJSON("modelCustom", "main", "")
        }
    }
}