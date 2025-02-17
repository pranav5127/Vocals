package com.app.sounds.viewmodel

sealed class Screen(val route: String) {
    object RecordSendAudioScreen : Screen("record_send_audio_screen")
    object UploadFileScreen: Screen("upload_file_screen")
    object PlayerScreen: Screen("player_screen")
}