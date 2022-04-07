package com.kadabengaran.storyapp.view

import android.app.Application
import androidx.lifecycle.*
import com.kadabengaran.storyapp.service.model.User
import com.kadabengaran.storyapp.service.model.UserPreference
import kotlinx.coroutines.launch

class PreferenceViewModel (application: Application) : AndroidViewModel(application) {

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
}