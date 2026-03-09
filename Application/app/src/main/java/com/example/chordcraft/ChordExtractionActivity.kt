package com.example.chordcraft

import android.net.Uri
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.chordcraft.components.ChordViewModel
import com.example.chordcraft.components.CreateFretBoards
import com.example.chordcraft.components.extractChords

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.example.chordcraft.ui.BorderBar
import com.example.chordcraft.components.filePickerLauncher
import com.example.chordcraft.components.getFileName
import com.example.chordcraft.ui.NavMenu
import com.example.chordcraft.ui.theme.ChordCraftTheme

private val ScreenPadding = 32.dp

class ChordExtractionActivity : ComponentActivity() {
    private val viewModel: ChordViewModel by lazy {
        (application as ChordCraftApplication).chordViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        setContent {
            ChordCraftTheme { ChordExtractionStructure(viewModel) }
        }
    }
}

@Composable
fun ChordExtractionStructure(
    viewModel: ChordViewModel,
    borderBar: @Composable (() -> Unit) = { BorderBar() },
) {
    val chordList by viewModel.chordList

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        borderBar()

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CreateFretBoards(chordList)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            UploadChord(
                viewModel = viewModel,
                modifier = Modifier.padding(ScreenPadding)
            )
        }

        NavMenu()
        borderBar()
    }
}

@Composable
fun UploadChord(
    viewModel: ChordViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedFileUri = remember { mutableStateOf<Uri?>(null) }
    val launchFilePickerCall = filePickerLauncher(selectedFileUri)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Button(onClick = launchFilePickerCall) {
            Text(text = "Upload Audio")
        }

        Text(
            text = ".MP3 or .WAV",
            style = MaterialTheme.typography.bodySmall
        )

        selectedFileUri.value?.let { uri ->
            Text(
                text = "Selected: ${getFileName(uri)}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Button(onClick = {
            val uri = selectedFileUri.value
            if (uri != null) {
                viewModel.chordList.value = extractChords(true, uri, context)
            }
        }) {
            Text("Generate Chords! (Python)")
        }

        val scope = rememberCoroutineScope()
        Button(onClick = {
            val uri = selectedFileUri.value
            if (uri != null) {
                scope.launch(Dispatchers.IO) {
                    viewModel.chordList.value = extractChords(false, uri, context)
                }
            }
        }) {
            Text("Generate Chords! (API)")
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ChordExtractionPreview() {
    val testViewModel = ChordViewModel().apply { chordList.value = emptyList() }
    ChordExtractionStructure(testViewModel)
}