import time, json
import librosa
import chordProcessing

def main(audioPath):
    processStart = time.time()

    # Load audio file
    y, sr = librosa.load(audioPath, sr=22050)
    
    # Compute Chromagram.
    chromagram = librosa.feature.chroma_cqt(
        y=y,            # Audio.
        sr=sr,          # Audio Sample Rate.
        hop_length=512, # Space between Frames.
        n_chroma=12     #Pitch Classes.
    )

    # Load Chord Templates.
    chordNames, chordTemplates = chordProcessing.buildChordTemplates()
    # Detect Chords.
    chords = chordProcessing.detectChords(chromagram, chordNames, chordTemplates, hopLength=512, sr=sr)
    
    # Print Output JSON.
    processingTime = (time.time() - processStart)
    processingTime = f"{int(processingTime % 60):02d}.{int((processingTime % 1) * 1000):03d} Seconds"
    output = {
        "chords": chords,
        "processing_time": processingTime
    }
    return output