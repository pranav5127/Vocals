package com.app.sounds.ui.view

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import com.app.sounds.viewmodel.RecorderViewModel
import com.app.sounds.viewmodel.Screen
import com.app.sounds.viewmodel.UploadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream


class RecordViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecorderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecorderViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordSendAudioScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val recorderViewModel: RecorderViewModel = viewModel(
        factory = RecordViewModelFactory(context)
    )
    val uploadViewModel: UploadViewModel = viewModel()
    val isRecording by recorderViewModel.isRecording.observeAsState(initial = false)
    val amplitude by recorderViewModel.amplitude.observeAsState(initial = 0f)
    val outputFile by recorderViewModel.outputFile.observeAsState(initial = null)

    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    // Function to upload file to server
    fun uploadFileToServer(file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            uploadViewModel.uploadAudioFile(file)
        }
    }

    // Function to get file name from URI
    fun getFileName(context: Context, uri: Uri): String {
        var fileName = ""
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    fileName = it.getString(nameIndex)
                }
            }
        } else {
            fileName = uri.path?.substring(uri.path!!.lastIndexOf("/") + 1) ?: "default_file_name"
        }
        return fileName
    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = getFileName(context, uri)

            // Check if the file is a .wav file
            if (!fileName.endsWith(".wav", ignoreCase = true)) {
                Toast.makeText(context, "Only .wav files are allowed", Toast.LENGTH_SHORT).show()
                return null
            }

            val tempFile = File(context.cacheDir, fileName)

            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Function to handle the picked audio file URI
    val pickAudioFile = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Ensure it's an internal storage file by checking the scheme (e.g., "content", "file")
            if (it.scheme == "content" || it.scheme == "file") {
                val file = getFileFromUri(context, it)
                if (file != null) {
                    uploadFileToServer(file)
                    navController.navigate("player_screen/${Uri.encode(file.absolutePath)}")
                }
            } else {
                Toast.makeText(context, "Invalid file selected. Please choose a .wav file from internal storage.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Stut Fix") },
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Upload File") },
                            onClick = {
                                expanded = false
                                showDialog = true
                                pickAudioFile.launch("audio/wav") // Only allow .wav file selection
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
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
                    colors = CardDefaults.cardColors(Color(41, 42, 42, 79))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        WaveformAnimation(amplitude * 20f)
                    }
                }
                Button(
                    onClick = {
                        if (isRecording) {
                            recorderViewModel.stopRecording()
                            recorderViewModel.setAmplitude()
                            outputFile?.let { file ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    uploadViewModel.uploadAudioFile(file)
                                }
                                val encodedPath = Uri.encode(file.absolutePath)
                                navController.navigate("player_screen/$encodedPath")
                            }
                        } else {
                            val file = File(context.cacheDir, "audio.wav")
                            recorderViewModel.startRecording(file)
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 48.dp)
                        .size(84.dp)
                        .clip(shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE91E47)),
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("File Upload") },
            text = { Text("File upload successful.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                }) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
