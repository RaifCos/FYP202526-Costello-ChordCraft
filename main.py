import numpy as np
import librosa
import argparse
import chordTemplates as ct
from scipy.ndimage import median_filter

chordNames = []
chordTemplates = []

# Function to Identify to find the best-fitting Chords for the music provided.
def recognizeChords(audioPath, smoothingWindow):
    # Load Audio.
    y, sr = librosa.load(audioPath, sr=None, mono=True)

    # Compute Chromagram.
    chroma = librosa.feature.chroma_cqt(
        y=y,            # Audio.
        sr=sr,          # Audio Sample Rate.
        hop_length=512, # Space between Frames (Time Between Frames = hop_length/sr).
        n_chroma=12     # Number of Pitch Classes (12 for standard western).
    )
    
    # Median Filtering to reduce Noise/Transients.
    chroma = np.array([median_filter(chroma[i], size=smoothingWindow)  for i in range(chroma.shape[0])])
    
    # Normalize each Frame.
    chroma = chroma / (np.linalg.norm(chroma, axis=0, keepdims=True) + 1e-8)

    # Compute Similarity between Templates and Chroma per frame
    similarities = np.dot(chordTemplates, chroma)
    bestIndices = np.argmax(similarities, axis=0)
    
    # Mode filtering to smooth chord changes (prevent rapidly alternating between Chords)
    if smoothingWindow > 1:
        filteredIndices = median_filter(bestIndices, size=smoothingWindow, mode='nearest')
        bestIndices = filteredIndices.astype(int)
    
    # Get the Best Chords.
    bestChords = [chordNames[i] for i in bestIndices]

    # Get Time for each Frame.
    hopTime = librosa.frames_to_time(np.arange(chroma.shape[1]), sr=sr, hop_length=512)
    return list(zip(hopTime, bestChords))

# Function to append Frames with the same Chords together. 
def getChordSegments(chordSequence, minDuration):
    if not chordSequence:
        return []
    
    segments = []
    currentChord = chordSequence[0][1]
    startTime = chordSequence[0][0]

    # Iterate through each Chord, starting a new segment if the Chord changes. 
    for i, (t, chord) in enumerate(chordSequence[1:], 1):
        if chord != currentChord:
            duration = t - startTime
            if duration >= minDuration:
                segments.append((startTime, t, currentChord, duration))
            currentChord = chord
            startTime = t
    
    # Create the Final segment.
    endTime = chordSequence[-1][0]
    duration = endTime - startTime
    if duration >= minDuration:
        segments.append((startTime, endTime, currentChord, duration))
    
    return segments

if __name__ == "__main__":
    # Read in Parameters
    parser = argparse.ArgumentParser()
    parser.add_argument("audioPath", help="Input Audio file.")
    parser.add_argument("smoothingWindow", help="Smoothing Window", default=5)
    parser.add_argument("minimumChordDuration", help="Minimum Chord Duration", default=0.5)
    args = parser.parse_args()

    audioPath = args.audioPath
    smoothingWindow = int(args.smoothingWindow)
    minimumChordDuration = float(args.minimumChordDuration)
    
    # Retrieve Chord Templates
    chordNames, chordTemplates = ct.buildChordTemplates()

    chordSequence = recognizeChords(audioPath, smoothingWindow)
    segments = getChordSegments(chordSequence, minimumChordDuration)

    print("Chord Progression:")
    print("-" * 50)
    for start, end, chord, duration in segments:
        print(f"{start:6.2f}s - {end:6.2f}s: {chord:8s} ({duration:.2f}s)")