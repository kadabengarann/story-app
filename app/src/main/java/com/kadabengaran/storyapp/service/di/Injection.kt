package com.kadabengaran.storyapp.service.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.kadabengaran.storyapp.service.StoryRepository
import com.kadabengaran.storyapp.service.remote.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }
}