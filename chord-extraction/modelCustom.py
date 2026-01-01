import time, json
import librosa
import argparse
import numpy as np
import chordProcessing

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

if __name__ == "__main__":
    processStart = time.time()

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
    chordNames, chordTemplates = chordProcessing.buildChordTemplates()
    # Detect Chords.
    chords = chordProcessing.detectChords(chromagram, chordNames, chordTemplates, hopLength=hopLength, sr=sr)

    # Print Output JSON.
    processingTime = (time.time() - processStart)
    processingTime = f"{int(processingTime % 60):02d}.{int((processingTime % 1) * 1000):03d} Seconds"
    output = {
        "chords": chords,
        "processing_time": processingTime
    }
    print(json.dumps(output, indent=4))