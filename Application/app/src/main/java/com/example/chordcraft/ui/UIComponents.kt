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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun ActivityHeader(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val title = when (navBackStackEntry?.destination?.route) {
        "extraction" -> "Chord Generation"
        "playback"   -> "Chord Playback"
        "live"       -> "Live Chord Generation"
        else         -> ""
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
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
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
    )
}

@Composable
fun NavMenu(navController: NavController) {
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
        Row(modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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

            Button(
                onClick = {
                    if (currentRoute != "live") {
                        navController.navigate("live")
                    }
                }
            ) {
                Icon(imageVector = iconSet.Create, contentDescription = "Live Chords")
            }
        }
    }
}

fun moveActivity(context: Context, target: Class<out Activity>) {
    val intent = Intent(context, target)
    context.startActivity(intent)
}