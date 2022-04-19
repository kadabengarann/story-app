package com.kadabengaran.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.StoryRepository
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.LoginResult
import com.kadabengaran.storyapp.service.model.RegisterBody


class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun register(user: RegisterBody): LiveData<Result<String>?> {
        return storyRepository.register(user).asLiveData()
    }

    fun login(user: LoginBody): LiveData<Result<LoginResult>?> {
        return storyRepository.login(user).asLiveData()
    }

}