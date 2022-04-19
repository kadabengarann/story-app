package com.kadabengaran.storyapp.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.StoryRepository
import com.kadabengaran.storyapp.service.model.StoryItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    var fetched = false

    private val _listStory = MutableLiveData<Result<List<StoryItem>>>()
    val listStory: LiveData<Result<List<StoryItem>>> = _listStory

    private val _tokenSession = MutableLiveData<String>()
    var tokenSession: LiveData<String> = _tokenSession

    fun getStories() {
        viewModelScope.launch {
            tokenSession.value?.let { token ->
                storyRepository.fetchStoryList(token).collect {
                    _listStory.postValue(it)
                }
            }
        }
        fetched = true
    }

    fun setToken(token: String) {
        _tokenSession.postValue(token)
    }
}