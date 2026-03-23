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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun ActivityHeader(
    navController: NavController,           // added
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val title = when (navBackStackEntry?.destination?.route) {
        "extraction" -> "Chord Generation"
        "playback"   -> "Chord Playback"
        else         -> ""
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun BorderBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun NavMenu(navController: NavController) {       // added navController param
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val iconSet = Icons.Default

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    if (currentRoute != "extraction") {
                        navController.navigate("extraction")
                    }
                }
            ) {
                Icon(imageVector = iconSet.Home, contentDescription = "Chord Extraction")
            }

            Button(
                onClick = {
                    if (currentRoute != "playback") {
                        navController.navigate("playback")
                    }
                }
            ) {
                Icon(imageVector = iconSet.PlayArrow, contentDescription = "Chord Playback")
            }
        }
    }
}

fun moveActivity(context: Context, target: Class<out Activity>) {
    val intent = Intent(context, target)
    context.startActivity(intent)
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