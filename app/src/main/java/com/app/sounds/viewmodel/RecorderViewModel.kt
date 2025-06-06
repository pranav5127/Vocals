package com.app.sounds.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sounds.utils.record.AndroidAudioRecorder
import kotlinx.coroutines.launch
import java.io.File

class RecorderViewModel(context: Context) : ViewModel() {
    private val audioRecorder = AndroidAudioRecorder(context)

    private val _isRecording = MutableLiveData(false)
    val isRecording: LiveData<Boolean> = _isRecording

    private val _amplitude = MutableLiveData(0f)
    val amplitude: LiveData<Float> = _amplitude

    private val _outputFile = MutableLiveData<File?>()  // Store the file reference
    val outputFile: LiveData<File?> = _outputFile

    fun startRecording(outputFile: File) {
        _outputFile.postValue(outputFile) // Store file reference
        viewModelScope.launch {
            audioRecorder.amplitudeListener = { amplitude ->
                _amplitude.postValue(amplitude)
            }
            audioRecorder.recordAudio(outputFile)
            _isRecording.postValue(true)
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            audioRecorder.stop()
            _isRecording.postValue(false)
        }
    }

    fun setAmplitude() {
        _amplitude.postValue(0f)
    }
}
