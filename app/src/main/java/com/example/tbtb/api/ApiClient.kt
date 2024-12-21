package com.example.tbtb.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // URL dasar API
    private const val BASE_URL =
        "https://api-unand-research-875600580548.asia-southeast2.run.app/" // Ganti dengan URL server API-mu

    // Konfigurasi OkHttpClient
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Waktu tunggu koneksi
            .readTimeout(30, TimeUnit.SECONDS)    // Waktu tunggu membaca data
            .writeTimeout(30, TimeUnit.SECONDS)   // Waktu tunggu menulis data
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Log level untuk debugging
            })
            .build()
    }

    // Inisialisasi Retrofit
    private val retrofit: Retrofit by lazy {
        synchronized(this) {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // Menggunakan OkHttpClient dengan interceptor
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    // Menghubungkan Retrofit dengan ApiService
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
