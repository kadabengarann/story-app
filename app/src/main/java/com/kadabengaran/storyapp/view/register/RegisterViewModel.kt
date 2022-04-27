package com.kadabengaran.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.LoginResult
import com.kadabengaran.storyapp.service.model.RegisterBody
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<String>?>()
    val registerResult: LiveData<Result<String>?> = _registerResult

    private lateinit var registerBody: RegisterBody
    private val _loginResult = MutableLiveData<Result<LoginResult>?>()
    val loginResult: LiveData<Result<LoginResult>?> = _loginResult

    fun register(user: RegisterBody) {
        registerBody = user
        viewModelScope.launch {
            storyRepository.register(user).collect {
                _registerResult.postValue(it)

                if (it is Result.Success)
                    login(LoginBody(registerBody.email, registerBody.password))
            }
        }
    }

    fun login(user: LoginBody) {
        viewModelScope.launch {
            storyRepository.login(user).collect {
                _loginResult.postValue(it)
            }
        }
    }

}