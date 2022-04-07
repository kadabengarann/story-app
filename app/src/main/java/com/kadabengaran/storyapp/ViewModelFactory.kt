package com.kadabengaran.storyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kadabengaran.storyapp.view.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.view.signup.RegisterViewModel
import com.kadabengaran.storyapp.service.model.UserPreference

class ViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass) {
        MainViewModel::class.java -> MainViewModel(pref)
        RegisterViewModel::class.java -> RegisterViewModel(pref)
        LoginViewModel::class.java -> LoginViewModel(pref)
        else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    } as T
}