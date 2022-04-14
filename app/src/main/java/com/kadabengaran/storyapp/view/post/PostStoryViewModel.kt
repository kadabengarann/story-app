package com.kadabengaran.storyapp.view.post

import android.accounts.AuthenticatorDescription
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.StoryRepository
import com.kadabengaran.storyapp.service.model.FileUploadResponse
import com.kadabengaran.storyapp.service.model.StoryItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostStoryViewModel (private val storyRepository: StoryRepository): ViewModel() {

    private val _uploadResult = MutableLiveData<Result<FileUploadResponse>>()
    val uploadResult: LiveData<Result<FileUploadResponse>> = _uploadResult


    private val _tokenSession = MutableLiveData<String>()
    val tokenSession: LiveData<String> = _tokenSession

    fun setToken(token:String) {
        _tokenSession.postValue(token)
    }
    fun getToken(): LiveData<String> {
        return _tokenSession
    }
    fun postStory(token:String, file: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            storyRepository.postStory(token, file, description).collect {
                _uploadResult.postValue(it)
            }
        }
    }
}