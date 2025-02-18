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
                    responseBody?.let {
                        try {
                            val json = JSONObject(it)
                            val status = json.optString("status", "unknown")
                            val result = json.optString("result", "No result")


                            val feedback = try {
                                val feedbackArray = json.optJSONArray("feedback")
                                val feedbackList = mutableListOf<String>()
                                feedbackArray?.let {
                                    for (i in 0 until it.length()) {
                                        feedbackList.add(it.getString(i))
                                    }
                                }
                                feedbackList
                            } catch (e: Exception) {
                                emptyList<String>()
                            }

                            if (status == "success") {
                                _uploadStatus.postValue(UploadState.Success(feedback, result))
                                Log.d("NETAPP", "Audio uploaded successfully: Feedback = $feedback, Result = $result")
                            } else {
                                _uploadStatus.postValue(UploadState.Error("Upload failed: $status"))
                                Log.e("NETAPP", "Audio upload failed: $status")
                            }
                        } catch (e: Exception) {
                            _uploadStatus.postValue(UploadState.Error("Invalid server response"))
                            Log.e("NETAPP", "Error parsing response", e)
                        }
                    } ?: run {
                        _uploadStatus.postValue(UploadState.Error("Empty server response"))
                        Log.e("NETAPP", "Empty response from server")
                    }
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
    data class Success(val feedback: List<String>, val result: String) : UploadState()
    data class Error(val message: String) : UploadState()
}
