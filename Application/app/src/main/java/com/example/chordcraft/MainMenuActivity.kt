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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.chordcraft.ui.components.BorderBar
import com.example.chordcraft.ui.components.filePickerLauncher
import com.example.chordcraft.ui.theme.ChordCraftTheme
import androidx.compose.material3.Button
import androidx.compose.runtime.*

private val ScreenPadding = 32.dp

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordCraftTheme { MainMenuStructure() }
        }
    }
}

@Composable
fun MainMenuStructure(
    borderBar: @Composable () -> Unit = { BorderBar() }
) {
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
            MainMenu(
            "Main Menu",
            "Options TBA",
            modifier = Modifier
                .padding(ScreenPadding)
            ) }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            UploadChord(
                modifier = Modifier
                    .padding(ScreenPadding)
            ) }

        borderBar()
    }
}

@Composable
fun MainMenu(
    txtA: String,
    txtB: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = txtA,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = txtB,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun UploadChord(
    modifier: Modifier = Modifier
) {
    val selectedFileUri = remember { mutableStateOf<Uri?>(null) }
    val launchFilePickerCall = filePickerLauncher(selectedFileUri)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Button(onClick = launchFilePickerCall) {
            Text("Upload Audio (MP3/WAV)")
        }

        selectedFileUri.value?.let { uri ->
            Text(
                text = "Selected: ${uri.lastPathSegment}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MainMenuPreview() {
    MainMenuStructure()
}