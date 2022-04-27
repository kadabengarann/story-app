package com.kadabengaran.storyapp.view.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.database.StoryEntity
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStories(): LiveData<PagingData<StoryEntity>> = storyRepository.getStories().cachedIn(viewModelScope)

    fun refresh() {
        viewModelScope.launch {
            storyRepository.refresh()
        }
    }
}