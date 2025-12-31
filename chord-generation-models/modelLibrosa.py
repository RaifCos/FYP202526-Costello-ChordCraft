import librosa
import argparse
import numpy as np
from chordProcessing import buildChordTemplates

def detectChords(chromagram, chordNames, chordTemplates, hopLength=512, sr=22050):
    n_frames = chromagram.shape[1]
    
    # Compute similarity between each frame and all templates
    # Using cosine similarity (dot product of normalized vectors)
    similarities = np.dot(chordTemplates, chromagram)
    
    # Get best matching chord for each frame
    best_chords = np.argmax(similarities, axis=0)
    
    # Convert frame indices to time
    frame_times = librosa.frames_to_time(np.arange(n_frames), sr=sr, hop_length=hopLength)
    
    # Group consecutive same chords
    chords = []
    current_chord = best_chords[0]
    start_time = 0
    
    for i in range(1, n_frames):
        if best_chords[i] != current_chord:
            # Chord changed
            end_time = frame_times[i]
            chord_name = chordNames[current_chord]
            
            # Only add chords that last at least 0.1 seconds
            if end_time - start_time >= 0.1:
                chords.append(f"{chord_name}")
            
            current_chord = best_chords[i]
            start_time = end_time
    
    # Add last chord
    chord_name = chordNames[current_chord]
    chords.append(f"{chord_name}")
    
    return chords

if __name__ == "__main__":
    # Read in Parameters
    parser = argparse.ArgumentParser()
    parser.add_argument("audioPath", help="Input Audio file.")
    args = parser.parse_args()
    audioPath = args.audioPath

    # Load audio file
    y, sr = librosa.load(audioPath, sr=22050)
    
    # Compute Chromagram.
    chroma = librosa.feature.chroma_cqt(
        y=y,            # Audio.
        sr=sr,          # Audio Sample Rate.
        hop_length=512, # Space between Frames (Time Between Frames = hop_length/sr).
        n_chroma=12     # Number of Pitch Classes (12 for standard western).
    )

    # Load Chord Templates.
    chordNames, chordTemplates = buildChordTemplates()
    # Detect Chords.
    chords = detectChords(chroma, chordNames, chordTemplates, hopLength=512, sr=sr)

    # Call Function to predict Chords.
    print("- Librosa Algorithm -")
    print(", ".join(chords))