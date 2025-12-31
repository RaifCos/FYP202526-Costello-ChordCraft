import librosa
import argparse
import numpy as np
from chordProcessing import buildChordTemplates

def ShortTimeFourierTransfomrm(y, sr, fftWindowSize=4096, hopLength=512):
    # Define Window as Hann function. 
    window = np.hanning(fftWindowSize)
    
    # Calculate number of Frames.
    n_frames = 1 + (len(y) - fftWindowSize) // hopLength
    
    # Calculate Short Time Fourier Transform.
    stft = np.zeros((fftWindowSize // 2 + 1, n_frames), dtype=complex)
    for i in range(n_frames):
        start = i * hopLength
        end = start + fftWindowSize
        
        # Extract frame and apply Hann Window.
        frame = y[start:end] * window
        
        # Compute Fast Fourier Transform.
        fft = np.fft.fft(frame)
        
        # Remove negative frequencies.
        stft[:, i] = fft[:fftWindowSize // 2 + 1]
    
    return stft

def buildChromagram(stft, sr, fftWindowSize=4096):
    n_bins, n_frames = stft.shape
    
    # Get Magnitude Spectrum and Frequency Bins.
    mag = np.abs(stft)
    freqs = np.fft.fftfreq(fftWindowSize, 1/sr)[:n_bins]
    
    # Set Chromagram to use Western 12-Tone scale.
    chromagram = np.zeros((12, n_frames))
    
    # Initalize Stuttgart Pitch. 
    stuttgart = 440.0
    
    # Map each Frequency bin to a Pitch Class.
    for bin_idx in range(n_bins):
        freq = freqs[bin_idx]
        
        # Skip DC and very low Frequencies.
        if freq < 80:
            continue
        
        # Calculate MIDI number and get Pitch. 
        midi = 69 + 12 * np.log2(freq / stuttgart)
        pitch = int(np.round(midi)) % 12
        
        # Add magnitude to corresponding Pitch Class.
        chromagram[pitch, :] += mag[bin_idx, :]
    
    # Normalize.
    for i in range(n_frames):
        norm = np.linalg.norm(chromagram[:, i])
        if norm > 0:
            chromagram[:, i] /= norm
    
    return chromagram

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
    
    # Parameters for STFT.
    fftWindowSize = 4096
    hopLength = 512
    
    # Compute Short Time Fourier Transform.
    stft = ShortTimeFourierTransfomrm(y, sr, fftWindowSize=fftWindowSize, hopLength=hopLength)
    # Build Chromagram.
    chromagram = buildChromagram(stft, sr, fftWindowSize=fftWindowSize)
    # Load Chord Templates.
    chordNames, chordTemplates = buildChordTemplates()
    # Detect Chords.
    chords = detectChords(chromagram, chordNames, chordTemplates, hopLength=hopLength, sr=sr)
    
    # Call Function to predict Chords.
    print("- Hardcoded Algorithm -")
    print(", ".join(chords))