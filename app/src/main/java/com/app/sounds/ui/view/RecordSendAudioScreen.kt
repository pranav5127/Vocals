package com.app.sounds.ui.view

import android.Manifest
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.sounds.ui.theme.SoundsTheme
import com.app.sounds.viewmodel.PlayerViewModel
import com.app.sounds.viewmodel.RecorderViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

class RecordViewModelFactory(private val context: Context): ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T{
            if(modelClass.isAssignableFrom(RecorderViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return RecorderViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")

        }
}

class PlayerViewModelFactory(private val context: Context): ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlayerViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordSendAudioScreen() {
    val context = LocalContext.current
    val recorderViewModel: RecorderViewModel = viewModel(
        factory = RecordViewModelFactory(context)
    )
    val isRecording by recorderViewModel.isRecording.observeAsState(initial = false)
    val amplitude by recorderViewModel.amplitude.observeAsState(initial = 0f)



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
             title = {
                 Text("Stut Fix")
             }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.99f)
                    .fillMaxHeight(0.5f),
                colors = CardDefaults.cardColors( Color(41, 42, 42, 79))

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    WaveformAnimation(amplitude)
                }
            }
                Button(
                    onClick = {
                     if(isRecording){
                         recorderViewModel.stopRecording()
                     } else {
                         val outputFile = File(context.filesDir, "audio.wav")
                         recorderViewModel.startRecording(outputFile)
                     }
                    },
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 48.dp)
                        .size(84.dp)
                        .clip(shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE91E63)),
                    border = BorderStroke(3.dp, Color(0xC8FFFFFF))

                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xff000000)

                    )
                }
            }
        }

    }

}



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AudioPermissionHandler() {
    val microphonePermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    // Request permission when the composable is first shown
    LaunchedEffect(key1 = Unit) {
        microphonePermissionState.launchPermissionRequest()
    }

    if (microphonePermissionState.status.isGranted) {
        // Permission granted: show your recording UI
        RecordSendAudioScreen()
    } else {
        // Permission denied: show a message explaining why the permission is needed
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Microphone permission is required to record audio.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { microphonePermissionState.launchPermissionRequest() }) {
                Text("Grant Permission")
            }
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun RecordSendAudioScreenPreview(){
    SoundsTheme(darkTheme = true) {
        RecordSendAudioScreen()
    }
}
