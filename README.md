# :musical_keyboard: ChordCraft - Frontend Code
ChordCraft is an Android Application built to help users identify chordal features in any audio provided. This project is divided into two main repositories:

- **Frontend Application** (Current Repo)
- **Backend API** ([Link](https://github.com/RaifCos/FYP202526-Costello-ChordCraft-Backend))

## :iphone: Frontend Application
This repository, which formally contained both the backend and frontend code, now contains only the code for the frontend Android application, created through the Android Studio IDE. 

## 	:guitar: Automatic Chord Recognition (ACR) Implementation 
The ChordCraft Application utilizes two differnt methods for Chord Extraction:
  - **ChordCraft API Call** - Makes a POST Request to the Backend API Service which implements the chord-CNN-LSTM AI model.
  - **Local "Lite" Model** - Calls a Script built into the Application. This model is less accurate with noisy audio, but works without an internet connection. 

The "Lite" Model is coded in Python scripts, which are managed within the application by the **Chaquopy** Plugin. The ```chordcraft/components/PythonManager.kt``` script manages any calls made to the Python Scripts by the application, which are all stored in the ```Application/app/src/main/python``` directory. The ```modelCustom.py``` script starts running the model by calling ```audioLoader.py``` to load the audio from the file, before using Short Time Fourier Transfomrm (STFT) to construct a Chromagram of the audio. 

Finally, the model calls ```chordProcessing.py``` to get the final output. The module first pre-generates Chord Templates based on different sets of intervals against a set of root notes, before dividing the Chormagram into frames, and comapring the frequency intervals to the intervals marked in the templates. The results are returned as a JSON-formatted string, which includes each chord detected in the audio processed, with timestamps denoting when in the audio the chord begins and ends. 

## :musical_note: Chord Playback Feature
The ChordCraft application has a "Chord Playback" feature that can generate a musical-recreation of the audio provided by the user. By analysing the chordal details returned by the ACR model, the app can use and combine individual notes from a preset soundbank to create a piano playback of the music uploaded by the user. The Chord Playback feature can be used to transcribe music from other instruments onto the piano, verify the accuracy of the ACR model, and provide users with a version of their audio without any background noise. 

## :round_pushpin: About this Application
The ChordCraft is being developed as part of University of Galway CT413 Final Year Project module FY25/26 with the project title "ChordCraft - Audio-to-Guitar Chords". 
- Student Name: Raif Costello
- SID: 22318961
