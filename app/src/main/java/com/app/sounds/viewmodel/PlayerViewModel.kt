package com.app.sounds.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sounds.utils.playback.AndroidAudioPlayer
import kotlinx.coroutines.launch
import java.io.File

class PlayerViewModel(context: Context) :ViewModel(){

    private val player = AndroidAudioPlayer(context)
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    fun playFile(file: File){
        viewModelScope.launch {
            player.playFile(file)
            _isPlaying.value = true
        }
    }
    fun stopPlaying(){
        viewModelScope.launch {
            player.stop()
            _isPlaying.value = false
        }

    }


}