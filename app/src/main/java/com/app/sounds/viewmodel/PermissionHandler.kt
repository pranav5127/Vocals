package com.app.sounds.viewmodel

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.app.sounds.ui.view.RecordSendAudioScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus

import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetPermission() {
    val micPermissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(key1 = micPermissionState.status) {
        if (micPermissionState.status != PermissionStatus.Granted){
            micPermissionState.launchPermissionRequest()
        }
    }


}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(){
    val micPermissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    if (micPermissionState.status != PermissionStatus.Granted){
       GetPermission()
    } else {
        RecordSendAudioScreen()
    }

}
