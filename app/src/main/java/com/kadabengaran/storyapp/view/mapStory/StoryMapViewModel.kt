package com.kadabengaran.storyapp.view.mapStory


import androidx.lifecycle.ViewModel
import com.kadabengaran.storyapp.service.data.StoryRepository

class StoryMapViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories() = storyRepository.fetchStoryLocation()
}
