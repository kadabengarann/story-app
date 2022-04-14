package com.kadabengaran.storyapp.view.login

import androidx.lifecycle.*
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.StoryRepository
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.LoginResult

class LoginViewModel(private  val storyRepository: StoryRepository) : ViewModel() {
    fun login(user: LoginBody): LiveData<Result<LoginResult>?>  {
        return storyRepository.login(user).asLiveData()
    }
}