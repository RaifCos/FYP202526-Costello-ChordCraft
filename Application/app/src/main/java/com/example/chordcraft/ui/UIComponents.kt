package com.example.chordcraft.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign

import com.example.chordcraft.ChordExtractionActivity
import com.example.chordcraft.ChordPlayingActivity
import org.json.JSONObject

@Composable
fun BorderBar(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun NavMenu (output: String) {
    val currContext = LocalContext.current
    val iconSet = Icons.Default
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button( { moveActivity(currContext, ChordExtractionActivity::class.java, output) } )
            {
                Icon(
                imageVector = iconSet.Home,
                contentDescription = "Chord Extraction"
            ) }

            Button( { moveActivity(currContext, ChordPlayingActivity::class.java, output) } )
            { Icon(
                imageVector = iconSet.PlayArrow,
                contentDescription = "Chord PLayback"
            ) }
        }
    }
}

@Composable
fun ChordDisplay(
chords: String,
modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Your Chords",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = chords,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun moveActivity(context: Context, target: Class<out Activity>, output: String) {
    val intent = Intent(context, target)
    intent.putExtra("output", output)
    context.startActivity(intent)
}