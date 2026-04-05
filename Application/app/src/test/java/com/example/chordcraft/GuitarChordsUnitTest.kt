package com.example.chordcraft

import android.content.Context
import android.net.Uri
import com.example.chordcraft.components.Chord
import com.example.chordcraft.components.ChordViewModel
import com.example.chordcraft.components.GuitarChord
import com.example.chordcraft.components.GuitarNote
import com.example.chordcraft.components.chordScore
import com.example.chordcraft.components.extractChords
import com.example.chordcraft.components.findBestGuitarChord
import com.example.chordcraft.components.generateGuitarChords
import com.example.chordcraft.components.getCandidatePositions
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito.mock

class GuitarChordsUnitTest {

    private fun testChord(vararg midiNotes: Int) = Chord(
        label = "Test",
        notes = midiNotes.toList(),
        startTime = 0.0,
        endTime = 1.0
    )

    @Test
    fun extractChords_ErrorMessage() {
        val mockContext = mock(Context::class.java)
        val mockUri = mock(Uri::class.java)
        val viewModel = ChordViewModel()

        val result = extractChords(
            localCall = true,
            uri = mockUri,
            context = mockContext,
            viewModel = viewModel
        )

        assertTrue(result.isEmpty())
        assertNotNull(viewModel.errorMessage.value)
    }

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

    @Test
    fun getCandidatePositions_UnderMaxFret() {
        val candidates = getCandidatePositions(60)
        assertTrue(candidates.all { it.fret <= 24 })
    }

    @Test
    fun getCandidatePositions_UniqueStrings() {
        val candidates = getCandidatePositions(60)
        val strings = candidates.map { it.string }
        assertEquals(strings.size, strings.distinct().size)
    }

    @Test
    fun findBestGuitarChord_FretSpanWithinLimit() {
        val chord = testChord(60, 64, 67) // C, E, G
        val candidates = chord.notes.map { getCandidatePositions(it) }
        val result = findBestGuitarChord(chord, candidates)
        assertNotNull(result)
        assertTrue(result!!.fretSpan <= 4)
    }

    @Test
    fun findBestGuitarChord_NoSharedStrings() {
        val chord = testChord(60, 64, 67)
        val candidates = chord.notes.map { getCandidatePositions(it) }
        val result = findBestGuitarChord(chord, candidates)
        assertNotNull(result)
        val strings = result!!.notes.map { it.string }
        assertEquals(strings.size, strings.distinct().size)
    }

    @Test
    fun findBestGuitarChord_ChordPersistence() {
        val chord = testChord(60, 64, 67)
        val candidates = chord.notes.map { getCandidatePositions(it) }
        val result = findBestGuitarChord(chord, candidates)
        assertEquals(chord, result?.chord)
    }

    @Test
    fun findBestGuitarChord_InvalidChord() {
        val chord = testChord(1, 2)
        val candidates = chord.notes.map { getCandidatePositions(it) }
        val result = findBestGuitarChord(chord, candidates)
        assertNull(result)
    }

    @Test
    fun chordScore_WrongScores() {
        val chord = testChord(60)
        val tightChord = GuitarChord(
            notes = listOf(GuitarNote(0, 1)),
            minFret = 1, maxFret = 2, fretSpan = 1, chord = chord
        )
        val wideChord = GuitarChord(
            notes = listOf(GuitarNote(0, 1)),
            minFret = 1, maxFret = 5, fretSpan = 4, chord = chord
        )
        assertTrue(chordScore(tightChord) < chordScore(wideChord))
    }

    @Test
    fun chordScore_CorrectScores() {
        val chord = testChord(60)
        val lowerChord = GuitarChord(
            notes = listOf(GuitarNote(0, 1)),
            minFret = 1, maxFret = 3, fretSpan = 2, chord = chord
        )
        val higherChord = GuitarChord(
            notes = listOf(GuitarNote(0, 5)),
            minFret = 5, maxFret = 7, fretSpan = 2, chord = chord
        )
        assertTrue(chordScore(lowerChord) < chordScore(higherChord))
    }

    @Test
    fun generateGuitarChords_SingleChord() {
        val chords = listOf(testChord(60, 64, 67)) // C:maj
        val result = generateGuitarChords(chords)
        assertEquals(1, result.size)
    }

    @Test
    fun generateGuitarChords_Order() {
        // Output list should maintain the same ordering as input.
        val chords = listOf<Chord>(
            testChord(60, 64, 67), // C:maj
            testChord(62, 65, 69)  // D:min
        )
        val result = generateGuitarChords(chords)
        assertEquals(2, result.size)
        assertEquals(chords[0], result[0]?.chord)
        assertEquals(chords[1], result[1]?.chord)
    }
}