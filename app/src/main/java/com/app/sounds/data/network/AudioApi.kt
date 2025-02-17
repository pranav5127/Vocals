package com.app.sounds.data.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AudioApi{
    @Multipart
    @POST("upload/")
    suspend fun uploadAudio(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>
}