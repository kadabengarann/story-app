package com.kadabengaran.storyapp.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kadabengaran.storyapp.service.model.User
import com.kadabengaran.storyapp.service.preferrences.UserPreference
import kotlinx.coroutines.launch

class PreferenceViewModel(application: Application) : AndroidViewModel(application) {

    private val pref = UserPreference(application)

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun saveSession(user: User) {
        viewModelScope.launch {
            pref.saveSession(user)
        }
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }
}