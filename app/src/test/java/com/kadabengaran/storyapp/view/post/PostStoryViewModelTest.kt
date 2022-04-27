package com.kadabengaran.storyapp.view.post

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kadabengaran.storyapp.DummyDatas
import com.kadabengaran.storyapp.MainCoroutineRule
import com.kadabengaran.storyapp.getOrAwaitValue
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.view.login.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PostStoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var postStoryViewModel: PostStoryViewModel
    private val dummyPostResponse = DummyDatas.generateDummyPostResponse()
    private lateinit var file: MultipartBody.Part
    @Before
    fun setUp() {
        postStoryViewModel = PostStoryViewModel(storyRepository)
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Test
    fun `when Upload Success Should Not Null and Return Success`() =  mainCoroutineRule.runBlockingTest  {
        file = mock(MultipartBody.Part::class.java)
        val dummyRequestBody= "dummy".toRequestBody()
        val expectedLoginResult = Result.Success(dummyPostResponse)
        val sampleResponse = flow{
            emit(Result.Success(dummyPostResponse))
        }

        Mockito.`when`(storyRepository.postStory(file,dummyRequestBody,dummyRequestBody,dummyRequestBody)).thenReturn(sampleResponse)
        postStoryViewModel.postStory(file,dummyRequestBody,dummyRequestBody,dummyRequestBody)
        val actualResult = postStoryViewModel.uploadResult.getOrAwaitValue()


        Mockito.verify(storyRepository).postStory(file,dummyRequestBody,dummyRequestBody,dummyRequestBody)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Success)
        Assert.assertEquals(expectedLoginResult.data, (actualResult as Result.Success).data)
    }



}