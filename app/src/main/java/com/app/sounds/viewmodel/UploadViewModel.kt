package com.app.sounds.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sounds.data.repository.AudioRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class UploadViewModel : ViewModel(){
    private val _uploadStatus = MutableLiveData<String>()
    val upload: LiveData<String> = _uploadStatus

    fun uploadAudioFile(file: File){
        viewModelScope.launch {
            try{
                val requestBody = RequestBody.create(MediaType.parse("audio/wav"), file)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val response = AudioRepository.instance.uploadAudio(filePart)

                if(response.isSuccessful){
                    _uploadStatus.postValue("Audio uploaded successfully")
                    Log.d("NETAPPSEND", "Audio uploaded successfully: ${response}")
                    Log.d("UPSTTAT", "$upload")
                }else   {
                    _uploadStatus.postValue("Error uploading audio: ${response.code()}")
                    Log.e("NETAPPSEND", "Audio uploaded failed: ${response}")
                    Log.e("UPSTTAT", "$upload")
                }

            }catch (e: Exception){
                _uploadStatus.postValue("Error uploading audio: ${e.message}")
                e.printStackTrace()
            }

        }
    }

}