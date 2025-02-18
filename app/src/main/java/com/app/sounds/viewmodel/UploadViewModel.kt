package com.app.sounds.viewmodel

import okhttp3.MediaType
import okhttp3.RequestBody
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sounds.data.repository.AudioRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.File

class UploadViewModel : ViewModel() {

    private val _uploadStatus = MutableLiveData<UploadState>()
    val upload: LiveData<UploadState> = _uploadStatus
    fun uploadAudioFile(file: File) {
        viewModelScope.launch {
            try {
                _uploadStatus.postValue(UploadState.Uploading)

                val requestBody = RequestBody.create(MediaType.parse("audio/wav"), file)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val response = AudioRepository.instance.uploadAudio(filePart)

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val message = responseBody?.let {
                        try {
                            JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            "Upload successful, but no message"
                        }
                    } ?: "Upload successful, but no message"

                    _uploadStatus.postValue(UploadState.Success(message))
                    Log.d("NETAPP", "Audio uploaded successfully: $message")
                } else {
                    _uploadStatus.postValue(UploadState.Error("Error ${response.code()}: ${response.message()}"))
                    Log.e("NETAPP", "Audio upload failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _uploadStatus.postValue(UploadState.Error(e.message ?: "Unknown error"))
                Log.e("NETAPP", "Exception during upload", e)
            }
        }
    }
}


sealed class UploadState {
    object Uploading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}



