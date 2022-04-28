package com.kadabengaran.storyapp.view.mapStory


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.model.StoryItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect


class StoryMapViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _listStory = MutableLiveData<Result<List<StoryItem>>>()
    val listStory: LiveData<Result<List<StoryItem>>> = _listStory

    fun getStories() {
        viewModelScope.launch {
            storyRepository.fetchStoryLocation().collect {
                _listStory.postValue(it)
            }
        }
    }

}
