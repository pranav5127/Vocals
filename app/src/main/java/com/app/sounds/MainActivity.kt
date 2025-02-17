package com.app.sounds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.sounds.ui.theme.SoundsTheme
import com.app.sounds.ui.view.AudioPermissionHandler


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoundsTheme {
                AudioPermissionHandler()
            }
        }
    }
}

