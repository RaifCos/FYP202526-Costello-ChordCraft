import time, json
import librosa
import argparse
import chordProcessing

if __name__ == "__main__":
    processStart = time.time()

    # Read in Parameters
    parser = argparse.ArgumentParser()
    parser.add_argument("audioPath", help="Input Audio file.")
    args = parser.parse_args()
    audioPath = args.audioPath

    # Load audio file
    y, sr = librosa.load(audioPath, sr=22050)
    
    # Compute Chromagram.
    chromagram = librosa.feature.chroma_cqt(
        y=y,            # Audio.
        sr=sr,          # Audio Sample Rate.
        hop_length=512, # Space between Frames (Time Between Frames = hop_length/sr).
        n_chroma=12     # Number of Pitch Classes (12 for standard western).
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
    print(json.dumps(output, indent=4))