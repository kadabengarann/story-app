package com.kadabengaran.storyapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kadabengaran.storyapp.view.login.LoginViewModel
import com.kadabengaran.storyapp.view.register.RegisterViewModel
import com.kadabengaran.storyapp.service.StoryRepository
import com.kadabengaran.storyapp.service.di.Injection
import com.kadabengaran.storyapp.view.home.HomeViewModel
import com.kadabengaran.storyapp.view.post.PostStoryViewModel

class ViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass) {
//        MainViewModel::class.java -> MainViewModel(pref)
        RegisterViewModel::class.java -> RegisterViewModel(storyRepository)
        LoginViewModel::class.java -> LoginViewModel(storyRepository)
        HomeViewModel::class.java -> HomeViewModel(storyRepository)
        PostStoryViewModel::class.java -> PostStoryViewModel(storyRepository)
        else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    } as T

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}