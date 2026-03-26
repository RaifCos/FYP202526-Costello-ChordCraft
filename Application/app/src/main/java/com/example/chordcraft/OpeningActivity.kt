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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
    borderBar: @Composable () -> Unit = { BorderBar(56) }
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
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
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
            text = "ChordCraft",
            fontSize = 60.sp,
            style = TextStyle(
                fontSize = 36.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Color.Gray,
                    offset = Offset(5.0f, 10.0f),
                    blurRadius = 5f
                )
            ),
            color = Color(0xFFFFFCFC),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Chord extraction made easy.",
            style = TextStyle(
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic,
            ),
            color = Color(0xFFFFFCFC),
        )
        Button(
            onClick = { onStartClick() },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2C0677)
            ),
            modifier = Modifier
                .padding(ScreenPadding)
                .height(56.dp)
                .fillMaxWidth(0.7f),
        ) {
            Text(
                text = "Start",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
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