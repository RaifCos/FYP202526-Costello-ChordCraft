package com.example.chordcraft

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
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
import com.example.chordcraft.components.liveRecordingHandler
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BorderBar(height=28)
        ActivityHeader(navController)

        // Fret Boards Element stays constant between menus.
        Box(
            modifier = Modifier
                .height(266.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CreateFretBoards(chordList)
        }

        // Alternating Extraction/Playback menus.
        NavHost(
            navController = navController,
            startDestination = "extraction",
            modifier = Modifier
                .padding(32.dp)
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
                    selectedFileUri)
            }
            composable("playback") {
                ChordPlayback(viewModel)
            }
            composable("live") {
                LiveRecorder(viewModel)
            }
        }

        NavMenu(navController)
        BorderBar(height=56)
    }
}

@Composable
fun UploadChord(
    viewModel: ChordViewModel,
    selectedFileUri: MutableState<Uri?>,
    modifier: Modifier = Modifier
) {
    // File Selection.
    val uri = selectedFileUri.value
    val context = LocalContext.current
    val launchFilePickerCall = filePickerLauncher(selectedFileUri)

    // Model Selection.
    val options = listOf("Simple", "Advanced")
    var selectedIndex by remember { mutableIntStateOf(0) }
    val isAdvanced = selectedIndex == 1
    val simpleText = "STFT-based model that is quicker but less accurate. Best suited for general analysis and playback."
    val advancedText = "AI model that generates much more accurate results. Best suited for professional annotation. *Requires an active internet connection."

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {

        Text(
            text = if (uri != null) "Audio Selected: ${getFileName(uri)}" else "Audio Selected: None",
            style = MaterialTheme.typography.bodyMedium,
            color = if (uri != null)
                MaterialTheme.colorScheme.onBackground
            else
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Text(
            text = "Upload an .MP3 or .WAV audio file and choose a model to begin generating chords.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Button(
            onClick = launchFilePickerCall
        ) {
            Text(text = "Select Audio")
        }

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { selectedIndex = index },
                    selected = selectedIndex == index
                ) {
                    Text(label)
                }
            }
        }

        Text(
            text = if (isAdvanced) advancedText else simpleText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                val uri = selectedFileUri.value
                if (uri != null) {
                    if (isAdvanced) {
                        scope.launch(Dispatchers.IO) {
                            viewModel.chordList.value = extractChords(false, uri, context)
                        }
                    } else {
                        viewModel.chordList.value = extractChords(true, uri, context)
                    }
                }
            }
        ) {
            Text("Generate Chords!")
        }
    }
}

@Composable
fun ChordPlayback(
    viewModel: ChordViewModel,
    modifier: Modifier = Modifier
) {
    val chordList by viewModel.chordList
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Generate and hear a recreation of your music in real time!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Button({ playbackChords(context, chordList) }) {
            Text(
                text = "Play Audio",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun LiveRecorder(
    viewModel: ChordViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isRecording by remember { mutableStateOf(false) }
    var liveRecording by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Get Permission for Recording.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Microphone permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission
                (context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = if (isRecording)
                "Listening for Chords..."
            else
                "Start playing and hear what chord you're playing in real time!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Button(
            onClick = {
                // Toggle Recording Mode.
                isRecording = !isRecording
                if (isRecording) {
                    // Start Recording (If audio permission is granted).
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        return@Button
                    }
                    // Start Live Recording Coroutine.
                    liveRecording = scope.launch(Dispatchers.IO) {
                        liveRecordingHandler(context, viewModel)
                    }
                } else {
                    // Stop Recording.
                    liveRecording?.cancel()
                    liveRecording = null
                }
            }
        ) {
            Text(
                text = if (isRecording) "Stop Recording" else "Start Recording",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    // Stop Live Recording Coroutine when menus are changed.
    DisposableEffect(Unit) {
        onDispose {
            liveRecording?.cancel()
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