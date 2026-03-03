package com.example.chordcraft

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
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.chordcraft.components.playbackAudio

import com.example.chordcraft.ui.BorderBar
import com.example.chordcraft.ui.ChordDisplay
import com.example.chordcraft.ui.NavMenu
import com.example.chordcraft.ui.theme.ChordCraftTheme

private val ScreenPadding = 32.dp

class ChordPlayingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordCraftTheme { ChordPlayingStructure() }
        }
    }
}

@Composable
fun ChordPlayingStructure(
    borderBar: @Composable () -> Unit = { BorderBar() },
    navMenu: @Composable () -> Unit = { NavMenu() }
) {
    var output by remember { mutableStateOf("Your Chords will appear here.") }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        val currentContext = LocalContext.current
        borderBar()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            ChordDisplay(
                output,
                modifier = Modifier.padding(ScreenPadding)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Button({ playbackAudio(currentContext) }) {
                Text(text = "Play Audio")
            }
        }

        navMenu()
        borderBar()
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ChordPlayingPreview() {
    ChordPlayingStructure()
}