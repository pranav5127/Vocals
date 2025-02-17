package com.app.sounds.data.repository



import com.app.sounds.data.network.AudioApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AudioRepository {

    private const val BASE_URL = "http://192.168.1.13:8000/"

   private val client = OkHttpClient.Builder()
       .connectTimeout(30, TimeUnit.SECONDS)
       .readTimeout(30, TimeUnit.SECONDS)
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
