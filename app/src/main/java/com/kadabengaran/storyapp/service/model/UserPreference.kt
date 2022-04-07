package com.kadabengaran.storyapp.service.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

    suspend fun saveUser(token: String) {
        context.dataStore.edit { preferences ->
//            preferences[SESSION_KEY] = token
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

    companion object {

        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val SESSION_KEY = stringPreferencesKey("password")
        private val STATE_KEY = booleanPreferencesKey("state")

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        /*fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }*/
    }
}