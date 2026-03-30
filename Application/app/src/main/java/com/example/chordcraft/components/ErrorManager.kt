package com.example.chordcraft.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

fun errorHandler(context: Context, msg: String, exception: Exception) {
    exception.message ?: "Unknown error."
    // Make sure Toast is called from main Thread.
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, "$msg$exception.message", Toast.LENGTH_LONG).show()
    }
}