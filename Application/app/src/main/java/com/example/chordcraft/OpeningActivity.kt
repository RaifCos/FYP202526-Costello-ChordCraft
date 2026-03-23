package com.example.chordcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import com.example.chordcraft.ui.BorderBar
import com.example.chordcraft.ui.moveActivity
import com.example.chordcraft.ui.theme.ChordCraftTheme

private val ScreenPadding = 32.dp

class OpeningActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordCraftTheme { OpeningStructure() }
        }
    }
}

@Composable
fun OpeningStructure(
    borderBar: @Composable () -> Unit = { BorderBar() }
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        val currContext = LocalContext.current
        borderBar()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
        Opening(
            onStartClick = { moveActivity(currContext, MainActivity::class.java) },
            modifier = Modifier
                .padding(ScreenPadding)
        ) }
        borderBar()
    }
}

@Composable
fun Opening(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Welcome to ChordCraft!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Chord extraction made easy.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Button(
            onClick = { onStartClick() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(ScreenPadding)
                .height(56.dp)
                .fillMaxWidth(0.7f),
        ) {
            Text(
                text = "Start",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun OpeningPreview() {
    OpeningStructure()
}