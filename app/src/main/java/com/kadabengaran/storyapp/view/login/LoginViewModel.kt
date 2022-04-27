package com.kadabengaran.storyapp.view.login

import androidx.lifecycle.*
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.model.FileUploadResponse
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.LoginResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import java.util.*

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