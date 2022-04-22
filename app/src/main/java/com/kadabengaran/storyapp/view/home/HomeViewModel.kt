package com.kadabengaran.storyapp.view.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.database.StoryEntity
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private var fetched = false

    private val _tokenSession = MutableLiveData<String>()
    var tokenSession: LiveData<String> = _tokenSession

    fun getStories(): LiveData<PagingData<StoryEntity>> =
        storyRepository.getStories("Bearer ${tokenSession.value}").cachedIn(viewModelScope)


    fun refresh() {
        viewModelScope.launch {
            storyRepository.refresh()
        }
    }

    fun setToken(token: String) {
        _tokenSession.postValue(token)
    }
}