package com.example.chordcraft

import android.net.Uri
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

import com.example.chordcraft.components.ChordViewModel
import com.example.chordcraft.components.CreateFretBoards
import com.example.chordcraft.components.extractChords
import com.example.chordcraft.components.filePickerLauncher
import com.example.chordcraft.components.getFileName
import com.example.chordcraft.components.playbackChords
import com.example.chordcraft.ui.ActivityHeader
import com.example.chordcraft.ui.BorderBar
import com.example.chordcraft.ui.NavMenu
import com.example.chordcraft.ui.theme.ChordCraftTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ChordViewModel by lazy {
        (application as ChordCraftApplication).chordViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        setContent {
            ChordCraftTheme { MainStructure(viewModel) }
        }
    }
}

@Composable
fun MainStructure(viewModel: ChordViewModel) {
    val navController = rememberNavController()
    val chordList by viewModel.chordList
    val selectedFileUri = remember { mutableStateOf<Uri?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        BorderBar()
        ActivityHeader(navController)

        // Fret Boards Element stays constant between menus.
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CreateFretBoards(chordList)
        }

        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            selectedFileUri.value?.let { uri ->
                Text(
                    text = "Audio: ${getFileName(uri)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Alternating Extraction/Playback menus.
        NavHost(
            navController = navController,
            startDestination = "extraction",
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            enterTransition = { slideInHorizontally { it } },
            exitTransition  = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition  = { slideOutHorizontally { it } },
        ) {
            composable("extraction") {
                UploadChord(
                    viewModel,
                    selectedFileUri,
                    modifier = Modifier.padding(32.dp))
            }
            composable("playback") {
                ChordPlayback(viewModel)
            }
        }

        NavMenu(navController)
        BorderBar()
    }
}

@Composable
fun UploadChord(
    viewModel: ChordViewModel,
    selectedFileUri: MutableState<Uri?>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launchFilePickerCall = filePickerLauncher(selectedFileUri)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Button(
            onClick = launchFilePickerCall
        ) {
            Text(text = "Select Audio")
        }

        Text(
            text = "Upload an .MP3 or .WAV audio file to begin generating chords.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Button(
            onClick = {
                val uri = selectedFileUri.value
                if (uri != null) {
                    viewModel.chordList.value = extractChords(true, uri, context)
                }
            }) {
            Text(
                text = "Simple",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text (
            text = "SIMPLE TEXT ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                val uri = selectedFileUri.value
                if (uri != null) {
                    scope.launch(Dispatchers.IO) {
                        viewModel.chordList.value = extractChords(false, uri, context)
                    }
                }
            }) {
                Text(
                    text = "ADVANCED TEXT",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

        Text (
            text = "Simple: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ChordPlayback(viewModel: ChordViewModel) {
    val chordList by viewModel.chordList
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button({ playbackChords(context, chordList) }) {
            Text(
                text = "Play Audio",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainPreview() {
    ChordCraftTheme {
        @Suppress("ViewModelConstructorInComposable")
        val testViewModel = ChordViewModel().apply { chordList.value = emptyList() }
        MainStructure(testViewModel)
    }
}