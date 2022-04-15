package com.kadabengaran.storyapp.service.di

import android.content.Context
import com.kadabengaran.storyapp.service.StoryRepository
import com.kadabengaran.storyapp.service.remote.ApiConfig


object Injection {
    fun provideRepository(): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }
}