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
import androidx.compose.runtime.setValue
import com.example.chordcraft.components.getChordTemplates

import com.example.chordcraft.ui.BorderBar
import com.example.chordcraft.ui.ChordDisplay
import com.example.chordcraft.ui.NavMenu
import com.example.chordcraft.ui.theme.ChordCraftTheme
import org.json.JSONObject

private val ScreenPadding = 32.dp

class ChordPlayingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chordString = intent.getStringExtra("chordString") ?: "Your Chords will appear here."
        val chordOutput = JSONObject(intent.getStringExtra("chordOutput") ?: """{"Error": "No Chords Found."}""")
        setContent {
            ChordCraftTheme { ChordPlayingStructure(chordOutput, chordString) }
        }
    }
}

@Composable
fun ChordPlayingStructure(
    chordModelOutput: JSONObject,
    chordModelString: String,
    borderBar: @Composable () -> Unit = { BorderBar() },
) {
    var chordOutput by remember { mutableStateOf(chordModelOutput) }
    var chordString by remember { mutableStateOf(chordModelString) }

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
            ChordDisplay(
                chordString,
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
            Button({ getChordTemplates(chordOutput) }) {
                Text(text = "Play Audio")
            }
        }

        NavMenu(chordOutput.toString(), chordString)
        borderBar()
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ChordPlayingPreview() {
    ChordPlayingStructure(JSONObject("""{"Error": "No Chords Found."}"""),"Your Chords will appear here.")
}