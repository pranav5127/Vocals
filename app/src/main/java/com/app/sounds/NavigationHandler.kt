package com.app.sounds

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.app.sounds.ui.view.UploadFileScreen
import com.app.sounds.ui.view.response.ResponseScreen
import com.app.sounds.viewmodel.PermissionHandler
import com.app.sounds.viewmodel.Screen

@Composable
fun NavigationHandler() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.RecordSendAudioScreen.route) {
        composable(Screen.RecordSendAudioScreen.route) {
            PermissionHandler(navController)
        }
        composable(Screen.UploadFileScreen.route) {
            UploadFileScreen()
        }
        composable("${Screen.PlayerScreen.route}/{filePath}") { backStackEntry ->
            val filePath = backStackEntry.arguments?.getString("filePath")
            if (filePath != null) {
                ResponseScreen(filePath)
            }
        }
    }
}
