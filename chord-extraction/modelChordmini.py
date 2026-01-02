import requests
import json

url = "https://chordmini-backend-191567167632.us-central1.run.app/api/recognize-chords"

def getChords(audioPath):
    with open(audioPath, "rb") as f:
        files = {"file": f}
        data = {"model": "chord-cnn-lstm"}
        response = requests.post(url, files=files, data=data)
        result = response.json()

    return result

def main(audioPath):
    return getChords(audioPath)
