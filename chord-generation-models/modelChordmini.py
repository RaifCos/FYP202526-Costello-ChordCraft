import requests
import argparse
import json

url = "https://chordmini-backend-191567167632.us-central1.run.app/api/recognize-chords"

def getChords(audioPath):
    with open(audioPath, "rb") as f:
        files = {"file": f}
        data = {"model": "chord-cnn-lstm"}
        response = requests.post(url, files=files, data=data)
        result = response.json()

    with open("chords.json", "w") as f:
        json.dump(result, f, indent=4)

    return data

if __name__ == "__main__":
    # Read in Parameters
    parser = argparse.ArgumentParser()
    parser.add_argument("audioPath", help="Input Audio file.")
    args = parser.parse_args()

    audioPath = args.audioPath
    chords = getChords(audioPath)
    print(chords)