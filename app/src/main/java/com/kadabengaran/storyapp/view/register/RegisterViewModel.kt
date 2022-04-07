package com.kadabengaran.storyapp.view.register

import androidx.lifecycle.*
import com.kadabengaran.storyapp.service.StoryRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.model.*


class RegisterViewModel(private  val storyRepository: StoryRepository) : ViewModel() {

    fun register(user: RegisterBody) : LiveData<Result<String>?>  {
        return storyRepository.register(user).asLiveData()

    }
    fun login(user: LoginBody): LiveData<Result<ResponseLogin>?>  {
        return storyRepository.login(user).asLiveData()

    }

}