package com.kadabengaran.storyapp.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.StoryRepository
import com.kadabengaran.storyapp.service.model.StoryItem

class ProfileViewModel (private val storyRepository: StoryRepository): ViewModel() {

    private val _listStory = MutableLiveData<Result<List<StoryItem>>>()
    val listStory: LiveData<Result<List<StoryItem>>> = _listStory

    private val _tokenSession = MutableLiveData<String>()
    val tokenSession: LiveData<String> = _tokenSession

}