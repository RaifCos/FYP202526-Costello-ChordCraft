package com.example.chordcraft.components

import androidx.compose.runtime.Composable

@Composable
fun createFretBoards(chordList: List<Chord>) {
    val guitarChords = generateGuitarChords(chordList)

    // TODO: Assemble Fret Boards into a Single UI Element (Horizontally Scrolling Menu?)
    for(guitarChord in guitarChords) {
        drawFretBoard(guitarChord)
    }
}

fun drawFretBoard(guitarChord: GuitarChord?) {
    // TODO: Draw Fret Board based on Guitar Chord Data
}