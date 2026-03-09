package com.example.chordcraft

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.chordcraft.components.ChordViewModel
import com.example.chordcraft.components.CreateFretBoards
import com.example.chordcraft.components.playbackChords

import com.example.chordcraft.ui.BorderBar
import com.example.chordcraft.ui.NavMenu
import com.example.chordcraft.ui.theme.ChordCraftTheme
import kotlin.getValue

class ChordPlayingActivity : ComponentActivity() {
    private val viewModel: ChordViewModel by lazy {
        (application as ChordCraftApplication).chordViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordCraftTheme { ChordPlayingStructure(viewModel) }
        }
    }
}

@Composable
fun ChordPlayingStructure(
    viewModel: ChordViewModel,
    borderBar: @Composable (() -> Unit) = { BorderBar() },
) {
    val chordList by viewModel.chordList
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        borderBar()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CreateFretBoards(chordList)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Button({ playbackChords(context, chordList) }) {
                Text(text = "Play Audio")
            }
        }

        NavMenu()
        borderBar()
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ChordPlayingPreview() {
    val testViewModel = ChordViewModel().apply { chordList.value = emptyList() }
    ChordPlayingStructure(testViewModel)
}