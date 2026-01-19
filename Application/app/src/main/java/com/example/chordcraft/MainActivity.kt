package com.example.chordcraft

import android.content.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.chordcraft.ui.theme.ChordCraftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordCraftTheme { Structure() }
        }
    }
}

@Composable
fun Structure() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val currContext = LocalContext.current
        GreetingText(
            "Welcome to ChordCraft",
            "chord extraction made easy.",
            onStartClick = { moveMenu(currContext) },
            modifier = Modifier
                .padding(32.dp)
        )
    }
}

@Composable
fun GreetingText(
    txtA: String,
    txtB: String,
    onStartClick: () -> Unit,
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
        Button(
            onClick = { onStartClick },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(32.dp)
                .width(256.dp)
                .height(64.dp)
        ) {
            Text(
                text = "Start",
                style = MaterialTheme.typography.headlineLarge
            )
        }
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

fun moveMenu(context: Context) {
    val intent = Intent(context, MainMenuActivity::class.java)
    context.startActivity(intent)
}