import numpy as np

roots = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"]

def buildChordTemplates():
    rootsLen = len(roots)
    chordTypes = {
        "": [0, 4, 7],              # Major triad
        "m": [0, 3, 7],             # Minor triad
        "dim": [0, 3, 6],           # Diminished triad
        "aug": [0, 4, 8],           # Augmented triad
        "7": [0, 4, 7, 10],         # Dominant 7th
        "maj7": [0, 4, 7, 11],      # Major 7th
        "m7": [0, 3, 7, 10],        # Minor 7th
        "dim7": [0, 3, 6, 9],       # Diminished 7th
        "m7b5": [0, 3, 6, 10],      # Half-diminished 7th
        "sus2": [0, 2, 7],          # Suspended 2nd
        "sus4": [0, 5, 7],          # Suspended 4th
    }
    
    chordNames = []
    chordTemplates = []
    
    # Generate Template for each Chord.
    for rootIndex in range(rootsLen):
        for chordType, intervals in chordTypes.items():

            # Generate Choord name from Root and Template.
            chordName = f"{roots[rootIndex]}{chordType}"
            chordNames.append(chordName)

            # Binary Row Vector (0 for when note is not in the chord, 1 when it is).
            template = np.zeros(rootsLen, dtype=float)
            for interval in intervals:
                template[(rootIndex + interval) % rootsLen] = 1.0
            chordTemplates.append(template)

    # Create Matrix of Chord Templates
    chordTemplates = np.vstack(chordTemplates).astype(float)

    # Normalize each Template Vector so the algorithm isn't bias towards Chords with more notes. 
    normalizedValues = np.linalg.norm(chordTemplates, axis=1, keepdims=True)
    normalizedValues[normalizedValues == 0] = 1.0 
    chordTemplates /= normalizedValues

    # Return Results.
    return chordNames, chordTemplates

def printResults():
    names, templates = buildChordTemplates()
    
    print("Chord Templates\n")
    
    for chordNames in names:
        idx = names.index(chordNames)
        template = templates[idx]
        # Find which notes are active (threshold for normalized values)
        notes = [i for i in range(12) if template[i] > 0.1]
        chordNotes = [roots[n] for n in notes]
            
        # Show binary pattern and note names
        binary = (template > 0.1).astype(int)
        binaryString = ''.join(str(b) for b in binary)
        print(f"{chordNames}: [{binaryString}]  -  {' + '.join(chordNotes)}")