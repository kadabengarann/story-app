package com.kadabengaran.storyapp.service.preferrences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kadabengaran.storyapp.service.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference (private val context: Context) {
   fun getUser(): Flow<User> {
        return context.dataStore.data.map { preferences ->
            User(
                preferences[NAME_KEY] ?:"",
                preferences[SESSION_KEY] ?:"",
                preferences[STATE_KEY] ?: false

            )
        }
    }

    suspend fun saveSession(user: User) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[SESSION_KEY] = user.token
            preferences[STATE_KEY] = true

        }
    }
    suspend fun login() {
        context.dataStore.edit { preferences ->
            preferences[STATE_KEY] = true
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = ""
            preferences[SESSION_KEY] = ""
            preferences[STATE_KEY] = false

        }
    }
    fun getThemeSetting(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }
    companion object {
        private val NAME_KEY = stringPreferencesKey("name")
        private val SESSION_KEY = stringPreferencesKey("password")
        private val STATE_KEY = booleanPreferencesKey("state")
        private val THEME_KEY = booleanPreferencesKey("theme_setting")
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    }
}