package com.app.sounds.ui.view.response

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.sounds.ui.view.audioplayer.AudioPlayer
import java.io.File


@Composable
fun ResponseScreen(filePath: String) {

    val file = File(filePath)
    val uri = file.absolutePath


    Scaffold {paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AudioPlayer(uri)

        }

    }
}