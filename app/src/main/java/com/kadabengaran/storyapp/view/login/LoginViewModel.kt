package com.kadabengaran.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.LoginResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResult>?>()
    val loginResult: LiveData<Result<LoginResult>?> = _loginResult

    fun login(user: LoginBody) {
        viewModelScope.launch {
            storyRepository.login(user).collect {
                _loginResult.postValue(it)
            }
        }
    }
}