package com.kadabengaran.storyapp.service.di

import android.content.Context
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.database.StoryDatabase
import com.kadabengaran.storyapp.service.remote.ApiConfig


object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService(context)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(storyDatabase, apiService)
    }
}