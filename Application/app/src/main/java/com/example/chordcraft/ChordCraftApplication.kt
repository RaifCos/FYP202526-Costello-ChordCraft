package com.example.chordcraft

import android.app.Application
import com.example.chordcraft.components.ChordViewModel

class ChordCraftApplication : Application() {
    val chordViewModel: ChordViewModel by lazy { ChordViewModel() }
}