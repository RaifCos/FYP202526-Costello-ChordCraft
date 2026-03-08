import numpy as np
import json

roots = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"]
_chordNames, _chordTemplates = None, None

def buildChordTemplates():
    rootsLen = len(roots)
    chordTypes = {
        ":maj": [0, 4, 7],           # Major triad
        ":min": [0, 3, 7],           # Minor triad
        ":dim": [0, 3, 6],           # Diminished triad
        ":aug": [0, 4, 8],           # Augmented triad
        ":7": [0, 4, 7, 10],         # Dominant 7th
        ":maj7": [0, 4, 7, 11],      # Major 7th
        ":m7": [0, 3, 7, 10],        # Minor 7th
        ":dim7": [0, 3, 6, 9],       # Diminished 7th
        ":m7b5": [0, 3, 6, 10],      # Half-diminished 7th
        ":sus2": [0, 2, 7],          # Suspended 2nd
        ":sus4": [0, 5, 7],          # Suspended 4th
    }
    
    chordNames = []
    chordTemplates = []
    
    # Generate Template for each Chord.
    for rootIndex in range(rootsLen):
        for chordType, intervals in chordTypes.items():

            # Generate Choord name from Root and Template.
            chordName = f"{roots[rootIndex]}{chordType}"
            chordNames.append(chordName)

            # Binary Vector to denote the corresponding nodes in each Chord. 
            template = np.zeros(rootsLen, dtype=float)
            for interval in intervals:
                template[(rootIndex + interval) % rootsLen] = 1.0
            chordTemplates.append(template)

    # Create Matrix of Chord Templates.
    chordTemplates = np.vstack(chordTemplates).astype(float)

    # Normalize each Chord Vector to eliminate bias towards Chords with more notes. 
    normalizedValues = np.linalg.norm(chordTemplates, axis=1, keepdims=True)
    normalizedValues[normalizedValues == 0] = 1.0 
    chordTemplates /= normalizedValues

    # Return Results.
    return chordNames, chordTemplates

def getChordTemplates(chordsJson):
    global _chordNames, _chordTemplates
    if _chordNames is None:
        _chordNames, _chordTemplates = buildChordTemplates()

    # Process Input JSON. 
    parsed = json.loads(chordsJson)
    chords = parsed["chords"]
    output = []

    for chord in chords:
        chordName = chord["chord"]

        # Skip "N" Chords. 
        if chordName == "N":
            continue

        # Generate MIDI Values for all Notes in the Interval. 
        if chordName in _chordNames:
            chordIndex = _chordNames.index(chordName)
            template = _chordTemplates[chordIndex]

            rootIndex = chordIndex % 12
            rootMidi = 60 + rootIndex

            allNotes = [60 + j for j, val in enumerate(template) if val > 0.0]
            midiIntervals = sorted([note if note >= rootMidi else note + 12 for note in allNotes])
        else:
            midiIntervals = []

        # Append Results to JSON. 
        output.append({
            "chord": chordName,
            "intervals": midiIntervals,
            "start": chord["start"],
            "end": chord["end"],
        })

    return json.dumps({"chords": output})
        

def detectChords(chromagram, hopLength=512, sr=22050):
    frameCount = chromagram.shape[1]

    global _chordNames, _chordTemplates
    if _chordNames is None:
        _chordNames, _chordTemplates = buildChordTemplates()

    # Calculate Cosine Similarity between each frame and chord templates.
    similarities = np.dot(_chordTemplates, chromagram)
    
    # Get best matching chord for each frame.
    bestChords = np.argmax(similarities, axis=0)
    
    # Convert frame indices to time.
    frameTimes = np.arange(frameCount) * hopLength / sr
    
    # Group consecutive identical chords
    chords = []
    currentChord = bestChords[0]
    startTime = 0
    
    for i in range(1, frameCount):
        if bestChords[i] != currentChord:
            endTime = frameTimes[i]
            # Only add chords that last at least 0.1 seconds.
            if endTime - startTime >= 0.1:
                chords.append({
                    "chord": _chordNames[currentChord],
                    "start": float(startTime),
                    "end": float(endTime),
                })            
            currentChord = bestChords[i]
            startTime = endTime
    
    # Add last chord.
    chords.append({
        "chord": _chordNames[currentChord],
        "start": float(startTime),
        "end": float(frameTimes[-1]),
    })
    return chords