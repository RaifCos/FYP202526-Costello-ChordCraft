package com.example.chordcraft

import com.example.chordcraft.components.GuitarNote
import com.example.chordcraft.components.generateGuitarChords
import com.example.chordcraft.components.getCandidatePositions
import org.junit.Test

import org.junit.Assert.*

class GuitarChordsUnitTest {

    @Test
    fun getCandidatePositions_AssertValidMIDI() {
        // All Combinations of C4
        val list = listOf<GuitarNote> (
            GuitarNote(1, 1),
            GuitarNote(2, 5),
            GuitarNote(3, 10),
            GuitarNote(4, 15),
            GuitarNote(5, 20),
        )
        assertEquals(list, getCandidatePositions(60))
    }

    @Test
    fun getCandidatePositions_AssertInvalidMIDI_Negative() {
        assertTrue(getCandidatePositions(-1).isEmpty())
    }

    @Test
    fun getCandidatePositions_AssertInvalidMIDI_TooSmall() {
        assertTrue(getCandidatePositions(1).isEmpty())
    }

    @Test
    fun getCandidatePositions_AssertInvalidMIDI_TooLarge() {
        assertTrue(getCandidatePositions(670).isEmpty())
    }

    @Test
    fun generateGuitarChords_AssertEmptyChords() {
        assertTrue(generateGuitarChords(emptyList()).isEmpty())
    }
}