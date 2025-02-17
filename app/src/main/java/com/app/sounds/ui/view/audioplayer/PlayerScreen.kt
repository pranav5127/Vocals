package com.app.sounds.ui.view.audioplayer

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.io.File

@Composable
fun rememberExoPlayer(context: Context, audioPath: String): ExoPlayer {
    val uri = if (audioPath.startsWith("http") || audioPath.startsWith("content")) {
        Uri.parse(audioPath)
    } else {
        Uri.fromFile(File(audioPath))
    }

    return remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }
}

@Composable
fun AudioPlayer(audioPath: String) {
    val context = LocalContext.current
    val player = rememberExoPlayer(context, audioPath)

    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    // Player state listener
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playWhenReady: Boolean) {
                isPlaying = playWhenReady
            }

            override fun onPlaybackStateChanged(state: Int) {
                duration = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L
            }
        }
        player.addListener(listener)
        onDispose { player.release() }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Audio Player", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { newPosition ->
                currentPosition = newPosition.toLong()
            },
            onValueChangeFinished = {
                player.seekTo(currentPosition)
            },
            valueRange = 0f..duration.toFloat()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { player.seekTo((player.currentPosition - 5000).coerceAtLeast(0)) }) {
                Icon(Icons.Default.FastRewind, contentDescription = "Rewind")
            }

            IconButton(onClick = {
                if (isPlaying) player.pause() else player.play()
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause"
                )
            }

            IconButton(onClick = { player.seekTo((player.currentPosition + 5000).coerceAtMost(duration)) }) {
                Icon(Icons.Default.FastForward, contentDescription = "Fast Forward")
            }
        }
    }

    // Update progress periodically
    LaunchedEffect(player) {
        while (isActive) {
            currentPosition = player.currentPosition
            delay(500)
        }
    }
}
