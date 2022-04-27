package com.kadabengaran.storyapp.view.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kadabengaran.storyapp.DummyDatas
import com.kadabengaran.storyapp.MainCoroutineRule
import com.kadabengaran.storyapp.getOrAwaitValue
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.data.StoryRepository
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.RegisterBody
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
class RegisterViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var registerViewModel: RegisterViewModel
    private val dummyRegisterResponse = "DummyDatas.generateDummyUserEntity()"
    private val dummyLoginResponse = DummyDatas.generateDummyUserEntity()

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(storyRepository)
    }
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `when Register Success Should Not Null and Return Success`() =  mainCoroutineRule.runBlockingTest  {
        val registerBody = RegisterBody("john doe","john@doe.com", "12345678")
        val expectedRegisterResult = Result.Success(dummyRegisterResponse)
        val sampleResponse = flow{
            emit(Result.Success(dummyRegisterResponse))
        }

        Mockito.`when`(storyRepository.register(registerBody)).thenReturn(sampleResponse)
        registerViewModel.register(registerBody)
        val actualResult = registerViewModel.registerResult.getOrAwaitValue()


        Mockito.verify(storyRepository).register(registerBody)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Success)
        Assert.assertEquals(expectedRegisterResult.data, (actualResult as Result.Success).data)
    }

    @Test
    fun `when Register Fail Should Not Null and Return Error`() =  mainCoroutineRule.runBlockingTest  {
        val registerBody = RegisterBody("john doe","johndoe.com", "12345")
        val sampleResponse = flow{
            emit(Result.Error("Error message"))
        }

        Mockito.`when`(storyRepository.register(registerBody)).thenReturn(sampleResponse)
        registerViewModel.register(registerBody)
        val actualResult = registerViewModel.registerResult.getOrAwaitValue()


        Mockito.verify(storyRepository).register(registerBody)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Error)
    }

    @Test
    fun `when Register Success Should Call Login and return Success`() =  mainCoroutineRule.runBlockingTest  {
        val registerBody = RegisterBody("john doe","john@doe.com", "12345678")
        val expectedLoginResult = Result.Success(dummyLoginResponse)
        val loginBody = LoginBody(registerBody.email, registerBody.password)
        val sampleResponse = flow{
            emit(Result.Success(dummyRegisterResponse))
        }
        val sampleLoginResponse = flow{
            emit(Result.Success(dummyLoginResponse))
        }
        Mockito.`when`(storyRepository.register(registerBody)).thenReturn(sampleResponse)
        Mockito.`when`(storyRepository.login(loginBody)).thenReturn(sampleLoginResponse)
        registerViewModel.register(registerBody)
        val actualResult = registerViewModel.loginResult.getOrAwaitValue()

        Mockito.verify(storyRepository).login(loginBody)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is Result.Success)
        Assert.assertEquals(expectedLoginResult.data, (actualResult as Result.Success).data)
    }
}