# Vocals App

A modern Android application built with Kotlin and Jetpack Compose for recording, uploading, and playing audio files. This application allows users to record their vocals, visualize audio waveforms in real time, upload WAV files to a backend server, and play audio with full control over playback.

## Features

- **Audio Recording:** Record audio using the device’s microphone.
- **Waveform Visualization:** Display a real-time animated waveform during recording.
- **Audio Upload:** Upload recorded or selected `.wav` files to a specified backend server.
- **Audio Playback:** Play audio files using ExoPlayer with play, pause, rewind, and fast-forward controls.
- **Permissions Handling:** Manage microphone permissions seamlessly with Accompanist Permissions.
- **Network Communication:** Use Retrofit and OkHttp for uploading audio to the server.

## Architecture Overview

The project is organized into several packages:

- **`com.app.sounds.data.network`:**  
  Contains the Retrofit API interface (`AudioApi`) that defines the endpoint for audio file uploads.

- **`com.app.sounds.data.repository`:**  
  Provides a singleton repository (`AudioRepository`) that initializes Retrofit with a base URL and network client settings.

- **`com.app.sounds.ui.view.audioplayer`:**  
  Implements the audio player UI using Jetpack Compose and ExoPlayer, including playback controls and a progress slider.

- **`com.app.sounds.ui.view`:**  
  Offers UI screens for recording and sending audio, managing file selection, and displaying waveform animations.

- **`com.app.sounds.utils.record`:**  
  Implements audio recording functionality with `AndroidAudioRecorder`, which captures PCM data, calculates amplitude, and converts recordings to WAV format.

- **`com.app.sounds.viewmodel`:**  
  Handles permission requests and integrates the ViewModel layer to coordinate recording and uploading logic.

## Setup & Installation

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/yourusername/sounds-app.git
   cd sounds-app
