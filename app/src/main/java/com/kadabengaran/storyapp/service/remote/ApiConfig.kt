package com.kadabengaran.storyapp.service.remote

import android.content.Context
import com.kadabengaran.storyapp.BuildConfig
import com.kadabengaran.storyapp.service.preferrences.UserPreference
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


    object ApiConfig {
        fun getApiService(context: Context): ApiService {
            val pref = UserPreference(context)

            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val headerInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()

                runBlocking {
                    pref.getSessionToken().let {
                        request.addHeader("Authorization", "Bearer $it")
                    }
                }
                chain.proceed(request.build())
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
