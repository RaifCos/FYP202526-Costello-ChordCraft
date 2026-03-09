package com.example.chordcraft.components

// Structure of each Guitar Note.
data class GuitarNote(
    val string: Int,
    val fret: Int,
)

// Structure of each fully assembled Guitar Chord.
data class GuitarChord(
    val notes: List<GuitarNote>,
    val minFret: Int,
    val maxFret: Int,
    val fretSpan: Int
)

// MIDI Numbers for each of the open strings (E4, B3, G3, D3, A2, E2)
val OPEN_NOTES = intArrayOf(64, 59, 55, 50, 45, 40)

private const val MAX_FRET = 24
private const val MAX_FRET_SPAN = 4 // Max stretch for a playable chord.

// Function to generate a list of Guitar Chords from a Chord JSON.
fun generateGuitarChords(chordList: List<Chord>): List<GuitarChord?> {
    val output = mutableListOf<GuitarChord?>()

    for (chord in chordList) {
        // Retrieve Chord from JSON.
        val chordNotes = chord.notes
        val midiNotes = (0 until chordNotes.size).map { chordNotes[it] }

        // Find all the positions where a particular note could be played.
        val candidatesPerNote: List<List<GuitarNote>> = midiNotes.map {
                midi -> getCandidatePositions(midi)
        }

        // Generate Chord and add to output.
        output.add( findBestGuitarChord(candidatesPerNote) )
    }
    return output
}

// Function to find all the possible positions a note can be played on the Guitar.
fun getCandidatePositions(midiNote: Int): List<GuitarNote> {
    val candidatePositions = mutableListOf<GuitarNote>()
    // Find the matching Fret on each string.
    for (i in 0 until OPEN_NOTES.size) {
        val fret = midiNote - OPEN_NOTES[i]
        if (fret in 0..MAX_FRET) { candidatePositions.add(GuitarNote(i, fret)) }
    }
    return candidatePositions
}

// Use candidate notes to generate candidate chords, then find the best-fitting Chord.
private fun findBestGuitarChord(candidatesPerNote: List<List<GuitarNote>>): GuitarChord? {
    var bestChord: GuitarChord? = null

    // Use a stack to store candidate chord notes.
    val stack = ArrayDeque<Pair<Int, List<GuitarNote>>>()
    stack.addLast(0 to emptyList())

    while (stack.isNotEmpty()) {
        val (noteIndex, current) = stack.removeLast()

        // Candidate chord has been formed, now evaluate if it is the best choice.
        if (noteIndex == candidatesPerNote.size) {
            val chord = buildChord(current)
            if (bestChord == null || chord.fretSpan < bestChord.fretSpan) { bestChord = chord }
            continue
        }

        // Evaluate candidate notes.
        for (candidate in candidatesPerNote[noteIndex]) {
            // Ensure no string collision and the fret span is within the limit..
            if (current.any { it.string == candidate.string }) continue
            val frets = (current.map { it.fret } + candidate.fret).filter { it > 0 }
            if (frets.size > 1 && (frets.max() - frets.min()) > MAX_FRET_SPAN) continue

            // If the candidate note is promising, add to the stack to explore further.
            stack.addLast(noteIndex + 1 to current + candidate)
        }
    }
    return bestChord
}

// Function to assemble a Guitar Chord from a given list of notes.
private fun buildChord(notes: List<GuitarNote>): GuitarChord {
    val frettedNotes = notes.filter { it.fret > 0 }
    val minFret = if (frettedNotes.isEmpty()) 0 else frettedNotes.minOf { it.fret }
    val maxFret = if (frettedNotes.isEmpty()) 0 else frettedNotes.maxOf { it.fret }
    return GuitarChord(
        notes = notes.sortedBy { it.string },
        minFret = minFret,
        maxFret = maxFret,
        fretSpan = maxFret - minFret
    )
}