package com.example.chordcraft.components

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext

import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Define Audio Format Constants.
private const val SAMPLE_RATE = 48000
private const val BIT_DEPTH = 16
private const val CHANNELS = 2

@Composable
fun filePickerLauncher(selectedFileUri: MutableState<Uri?>): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        // Check the File exists and is the correct format.
            uri: Uri? ->
        if (uri != null && formatValidation(context, uri)) {
            selectedFileUri.value = uri
            Toast.makeText(context, "File selected: $uri", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error Selecting File. Make sure it is .MP3 or .WAV", Toast.LENGTH_LONG).show()
        }
    } // Return a lambda callable from the Main Menu.
    return { launcher.launch("audio/*") }
}

fun formatValidation(context: Context, uri: Uri): Boolean {

    // Check File Type to confirm user uploaded a MP3 or WAV file.
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri)
    val isValidMime = mimeType == "audio/mpeg" ||  // .mp3
            mimeType == "audio/wav"   ||  // some devices
            mimeType == "audio/x-wav"

    // Check File Extension to Confirm.
    val extension = MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(mimeType)
        ?: uri.toString().substringAfterLast('.', "").lowercase()
    val isValidExtension = extension == "mp3" || extension == "wav"

    return isValidMime || isValidExtension
}

@Composable
fun getFileName(uri: Uri): String {
    val context = LocalContext.current
    var result = ""
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) { result = it.getString(nameIndex) }
            }
        }
    }
    return result
}

fun cacheFileFromURI(context: Context, uri: Uri, name: String): File {
    val tempFile = File(context.cacheDir, name)

    context.contentResolver.openInputStream(uri)?.use { input ->
        tempFile.outputStream().use { output -> input.copyTo(output) }
    }
    return tempFile
}

// Function to parse WAV data and convert to Float for sample mixing.
fun readWAV(bytes: ByteArray): FloatArray? {
    if(bytes.isEmpty()) { return null }

    val buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

    // Check format header to confirm the file is WAV.
    val riff = String(bytes, 0, 4)
    if (riff != "RIFF") return null
    buf.position(8)
    val wave = String(bytes, 8, 4)
    if (wave != "WAVE") return null

    // Prepare to read Audio specs ("fmt") Chunk.
    var audioFormat = 0;
    var numChannels = 0
    var sampleRate = 0;
    var bitsPerSample = 0

    // Prepare to read Data Chunk.
    var dataOffset = 0;
    var dataSize = 0

    buf.position(12)
    while (buf.remaining() > 8) {
        val chunkId = String(bytes, buf.position(), 4)
        buf.position(buf.position() + 4)
        val chunkSize = buf.int

        // Read Data of each Chunk.
        when (chunkId) {
            "fmt " -> {
                audioFormat  = buf.short.toInt() and 0xFFFF
                numChannels  = buf.short.toInt() and 0xFFFF
                sampleRate   = buf.int
                buf.int  // Byte Rate / Bytes per Second.
                buf.short // Block Align / Bytes per Frame.
                bitsPerSample = buf.short.toInt() and 0xFFFF
                if (chunkSize > 16) buf.position(buf.position() + chunkSize - 16)
            }
            "data" -> {
                dataOffset = buf.position()
                dataSize   = chunkSize
                break
            }
            else -> buf.position(buf.position() + chunkSize)
        }
    }

    // Convert Samples to Float.
    val frameCount = dataSize / (numChannels * 2)
    val samples = FloatArray(frameCount * numChannels)
    buf.position(dataOffset)

    // Normalize Samples for mixer.
    for (i in 0 until frameCount * numChannels) {
        samples[i] = buf.short.toFloat() / Short.MAX_VALUE
    }
    return samples
}

// Function to write a 16-bit PCM WAV file.
fun writeWav(pcm: ShortArray, file: File) {
    val dataSize = (pcm.size * 2)

    // Specify Bytes per Second and Frame.
    val bytesPerSec = SAMPLE_RATE * CHANNELS * BIT_DEPTH / 8
    val bytesPerFrame = CHANNELS * BIT_DEPTH / 8

    FileOutputStream(file).use { fos ->
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)

        // Specify RIFF format.
        header.put("RIFF".toByteArray())
        header.putInt(36 + dataSize)
        header.put("WAVE".toByteArray())

        // Specify Audio specs.
        header.put("fmt ".toByteArray())
        header.putInt(16)
        header.putShort(1)
        header.putShort(CHANNELS.toShort())
        header.putInt(SAMPLE_RATE)
        header.putInt(bytesPerSec)
        header.putShort(bytesPerFrame.toShort())
        header.putShort(BIT_DEPTH.toShort())

        // Write Audio data.
        header.put("data".toByteArray())
        header.putInt(dataSize)

        // Write headers to WAV File.
        fos.write(header.array())
        val sampleBuf = ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN)
        pcm.forEach { sampleBuf.putShort(it) }
        fos.write(sampleBuf.array())
    }
}