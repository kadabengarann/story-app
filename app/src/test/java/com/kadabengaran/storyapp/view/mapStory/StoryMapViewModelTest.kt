package com.kadabengaran.storyapp.view.mapStory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.kadabengaran.storyapp.DummyDatas
import com.kadabengaran.storyapp.getOrAwaitValue
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.model.StoryItem
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StoryMapViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var storyMapViewModel: StoryMapViewModel
    private val dummyStories = DummyDatas.generateDummyStories()

    @Before
    fun setUp() {
        storyMapViewModel = StoryMapViewModel(storyRepository)
    }

    @Test
    fun `when Get StoryOnMap Should Not Null and Return Success`() {
        val expectedStories = MutableLiveData<Result<List<StoryItem>>>()
        expectedStories.value = Result.Success(dummyStories)
        Mockito.`when`(storyMapViewModel.getStories()).thenReturn(expectedStories)

        val actualStories = storyMapViewModel.getStories().getOrAwaitValue()

        Mockito.verify(storyRepository).fetchStoryLocation()
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is Result.Success)
        Assert.assertEquals(dummyStories.size, (actualStories as Result.Success).data.size)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val headlineNews = MutableLiveData<Result<List<StoryItem>>>()
        headlineNews.value = Result.Error("Error")
        Mockito.`when`(storyMapViewModel.getStories()).thenReturn(headlineNews)

        val actualStories = storyMapViewModel.getStories().getOrAwaitValue()
        Mockito.verify(storyRepository).fetchStoryLocation()
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is Result.Error)
    }
}