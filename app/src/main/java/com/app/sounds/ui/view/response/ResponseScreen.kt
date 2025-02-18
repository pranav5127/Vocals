package com.app.sounds.ui.view.response

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.sounds.ui.view.audioplayer.AudioPlayer
import com.app.sounds.viewmodel.Screen
import com.app.sounds.viewmodel.UploadState
import com.app.sounds.viewmodel.UploadViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponseScreen(filePath: String, navController: NavController) {

    val file = File(filePath)
    val uri = file.absolutePath
    val uploadViewModel: UploadViewModel = viewModel()
    val uploadStatus = uploadViewModel.upload.observeAsState(initial = null).value

    androidx.compose.runtime.LaunchedEffect(Unit) {
        uploadViewModel.uploadAudioFile(file)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Results")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.RecordSendAudioScreen.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Audio Player Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.2f),
                colors = CardDefaults.cardColors(Color(41, 42, 42, 79))
            ) {
                AudioPlayer(uri)
            }

            Spacer(Modifier.height(24.dp))

            // Upload Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.4f),
                colors = CardDefaults.cardColors(Color(41, 42, 42, 79))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (uploadStatus) {
                        null, is UploadState.Uploading -> {
                            Text("Uploading...", color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        is UploadState.Success -> {

                            LazyColumn {
                                item {
                                    Text(
                                        text = (uploadStatus as UploadState.Success).message,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                        is UploadState.Error -> {
                            Text(
                                text = "Upload Failed: ${(uploadStatus as UploadState.Error).message}",
                                color = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate(Screen.RecordSendAudioScreen.route) },
                colors = ButtonDefaults.buttonColors(Color(191, 214, 254))
            ) {
                Text("Record Again")
            }
        }
    }
}
