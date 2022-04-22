package com.kadabengaran.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.LoginResult

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun login(user: LoginBody): LiveData<Result<LoginResult>?> {
        return storyRepository.login(user).asLiveData()
    }
}