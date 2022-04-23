package com.kadabengaran.storyapp.view.mapStory


import androidx.lifecycle.viewModelScope
import com.kadabengaran.storyapp.service.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.database.StoryEntity
import com.kadabengaran.storyapp.service.model.StoryItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class StoryMapViewModel (private val storyRepository: StoryRepository) : ViewModel() {

    var fetched = false

    private val _tokenSession = MutableLiveData<String>()
    var tokenSession: LiveData<String> = _tokenSession

//    fun getStories(): LiveData<PagingData<StoryEntity>> =
//        storyRepository.getStories("Bearer ${tokenSession.value}").cachedIn(viewModelScope)


    fun refresh() {
        viewModelScope.launch {
            storyRepository.refresh()
        }
    }

    private val _listStory = MutableLiveData<Result<List<StoryItem>>>()
    val listStory: LiveData<Result<List<StoryItem>>> = _listStory

    fun getStories() {
        viewModelScope.launch {
            tokenSession.value?.let { token ->
                storyRepository.fetchStoryLocation(token).collect {
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
