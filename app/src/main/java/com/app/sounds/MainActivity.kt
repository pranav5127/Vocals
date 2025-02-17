package com.app.sounds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.sounds.ui.theme.SoundsTheme
import com.app.sounds.ui.view.RecordSendAudioScreen
import com.app.sounds.viewmodel.GetPermission
import com.app.sounds.viewmodel.PermissionHandler
import com.google.accompanist.permissions.rememberPermissionState


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoundsTheme {
                PermissionHandler()
            }
        }
    }
}

