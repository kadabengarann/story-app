package com.kadabengaran.storyapp.view.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.StoryRepository
import com.kadabengaran.storyapp.service.model.FileUploadResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<FileUploadResponse>?>()
    val uploadResult: MutableLiveData<Result<FileUploadResponse>?> = _uploadResult

    private val _tokenSession = MutableLiveData<String>()

    fun getToken(): LiveData<String> {
        return _tokenSession
    }

    fun postToken(s: String) {
        return _tokenSession.postValue(s)
    }

    fun postStory(file: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            getToken().value?.let { token ->
                storyRepository.postStory(token, file, description).collect {
                    _uploadResult.postValue(it)
                }
            }
        }
    }

    fun resetProgress() {
        _uploadResult.value = null
    }
}