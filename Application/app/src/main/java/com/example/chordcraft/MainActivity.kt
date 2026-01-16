package com.example.chordcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.chordcraft.ui.theme.ChordCraftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Structure()
        }
    }
}

@Composable
fun Structure() {
    ChordCraftTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            GreetingText(
                "Welcome to ChordCraft",
                "chord extraction made easy.",
                modifier = Modifier
                    .padding(32.dp)
            )
        }
    }
}

@Composable
fun GreetingText(txtA: String, txtB: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = txtA,
            fontSize = 50.sp,
            lineHeight = 60.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = txtB,
            fontSize = 16.sp,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ChordCraftPreview() {
    Structure()
}