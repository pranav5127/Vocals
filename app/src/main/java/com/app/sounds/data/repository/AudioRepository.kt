package com.app.sounds.data.repository

import com.app.sounds.data.network.AudioApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AudioRepository {

    private const val BASE_URL = " https://faa2-2409-4064-4e4e-1cf1-baf1-ad4b-ad-f3bd.ngrok-free.app"

   private val client = OkHttpClient.Builder()
       .connectTimeout(120, TimeUnit.SECONDS)
       .readTimeout(120, TimeUnit.SECONDS)
       .build()

    val instance: AudioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AudioApi::class.java)
    }


}
