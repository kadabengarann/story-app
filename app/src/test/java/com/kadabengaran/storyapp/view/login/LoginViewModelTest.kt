package com.kadabengaran.storyapp.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kadabengaran.storyapp.DummyDatas
import com.kadabengaran.storyapp.MainCoroutineRule
import com.kadabengaran.storyapp.getOrAwaitValue
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.model.LoginBody
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dummyLoginResponse = DummyDatas.generateDummyUserEntity()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(storyRepository)
    }
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `when Login Success Should Not Null and Return Success`() =  mainCoroutineRule.runBlockingTest  {
        val loginBody = LoginBody("john@doe.com", "12345678")
        val expectedLoginResult = Result.Success(dummyLoginResponse)
        val sampleResponse = flow{
            emit(Result.Success(dummyLoginResponse))
        }

        Mockito.`when`(storyRepository.login(loginBody)).thenReturn(sampleResponse)
        loginViewModel.login(loginBody)
        val actualResult = loginViewModel.loginResult.getOrAwaitValue()


        Mockito.verify(storyRepository).login(loginBody)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Success)
        Assert.assertEquals(expectedLoginResult.data, (actualResult as Result.Success).data)
    }

    @Test
    fun `when Login Fail Should Not Null and Return Error`() =  mainCoroutineRule.runBlockingTest  {
        val loginBody = LoginBody("johndoe.com", "1236")
        val sampleResponse = flow{
            emit(Result.Error("Error message"))
        }

        Mockito.`when`(storyRepository.login(loginBody)).thenReturn(sampleResponse)
        loginViewModel.login(loginBody)
        val actualResult = loginViewModel.loginResult.getOrAwaitValue()

        Mockito.verify(storyRepository).login(loginBody)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Error)
    }
}