import json
import time
import numpy as np
import audioLoader
import chordProcessing

def ShortTimeFourierTransform(y, sr, fftWindowSize=4096, hopLength=512):
    window = np.hanning(fftWindowSize)

    # Calculate number of Frames.
    n_frames = 1 + (len(y) - fftWindowSize) // hopLength

    # Build 2D Array using frames to reduce memory usage.
    shape = (n_frames, fftWindowSize)
    strides = (y.strides[0] * hopLength, y.strides[0])
    frames = np.lib.stride_tricks.as_strided(y, shape=shape, strides=strides)

    # Apply Window and compute FFT.
    stft = np.fft.rfft(frames * window, axis=1).T.astype(np.complex64)
    return stft

def buildChromagram(stft, sr, fftWindowSize=4096):
    n_bins, n_frames = stft.shape

    # Get Frequencies.
    freqs = np.fft.rfftfreq(fftWindowSize, 1 / sr)
    stuttgart = 440.0

    # Compute Pitch Bins.
    valid = (freqs >= 80) & (freqs <= 5000)
    valid[0] = False

    midi = np.where(valid, 69 + 12 * np.log2(np.where(valid, freqs, 1.0) / stuttgart), -1.0)
    pitch_bins = np.where(valid, np.round(midi).astype(np.int32) % 12, -1)

    # Accumulate Magnitudes into Chromagram.
    mag = np.abs(stft)
    chromagram = np.zeros((12, n_frames), dtype=np.float32)

    for pitch in range(12):
        mask = pitch_bins == pitch
        if mask.any():
            chromagram[pitch] = mag[mask].sum(axis=0)

    # Normalise Frames.
    norms = np.linalg.norm(chromagram, axis=0, keepdims=True)
    norms[norms == 0] = 1
    chromagram /= norms

    return chromagram

def main(audioPath):
    processStart = time.time()
    y, sr = audioLoader.loadAudio(audioPath, targetSr=44100)
    
    # Parameters for STFT.
    fftWindowSize = 4096
    hopLength = 512
    
    # Compute Short Time Fourier Transform and Build Chromagram to detect Chords.
    stft = ShortTimeFourierTransform(y, sr, fftWindowSize=fftWindowSize, hopLength=hopLength)
    chromagram = buildChromagram(stft, sr, fftWindowSize=fftWindowSize)
    del stft
    import gc
    gc.collect()
    chords = chordProcessing.detectChords(chromagram, hopLength=hopLength, sr=sr)

    # Print Output JSON.
    processingTime = (time.time() - processStart)
    processingTime = f"{int(processingTime % 60):02d}.{int((processingTime % 1) * 1000):03d} Seconds"
    output = {
        "chords": chords,
        "processing_time": processingTime
    }
    return json.dumps(output)