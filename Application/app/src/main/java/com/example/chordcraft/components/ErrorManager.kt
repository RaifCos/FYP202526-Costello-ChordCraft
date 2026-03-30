package com.example.chordcraft.components

import android.content.Context
import android.widget.Toast

fun errorHandler(context: Context, msg: String, exception: Exception) {
    exception.message ?: "Unknown error."
    Toast.makeText(context, "$msg$exception.message", Toast.LENGTH_LONG).show()
}