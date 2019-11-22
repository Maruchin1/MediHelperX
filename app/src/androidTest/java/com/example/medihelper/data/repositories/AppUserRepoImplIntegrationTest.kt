package com.example.medihelper.data.repositories

import androidx.test.platform.app.InstrumentationRegistry
import com.example.medihelper.AppAndroidTest
import com.example.medihelper.data.di.retrofitTestModule
import com.example.medihelper.data.local.DeletedHistory
import com.example.medihelper.data.local.SharedPref
import com.example.medihelper.data.remote.ApiResponseMapper
import com.example.medihelper.data.remote.api.AuthenticationApi
import com.example.medihelper.data.remote.api.RegisteredUserApi
import com.example.medihelper.data.remote.dto.LoginResponseDto
import com.example.medihelper.domain.entities.ApiResponse
import com.example.medihelper.domain.entities.AppMode
import com.example.medihelper.domain.repositories.AppUserRepo
import com.google.common.truth.Truth
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.inject
import org.koin.dsl.module
import retrofit2.Retrofit

class AppUserRepoImplIntegrationTest : AppAndroidTest() {

    private val testModule = module {
        single {
            SharedPref(androidContext())
        }
        single {
            get<Retrofit>().create(AuthenticationApi::class.java)
        }
        single {
            get<Retrofit>().create(RegisteredUserApi::class.java)
        }
        single {
            ApiResponseMapper()
        }
        single {
            DeletedHistory(androidContext())
        }
        single {
            AppUserRepoImpl(
                context = androidContext(),
                sharedPref = get(),
                authenticationApi = get(),
                registeredUserApi = get(),
                apiResponseMapper = get(),
                deletedHistory = get()
            ) as AppUserRepo
        }
    }

    private val mockWebServer = MockWebServer()
    private val appUserRepo: AppUserRepo by inject()

    @Before
    fun before() {
        mockWebServer.start(8080)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        startKoin {
            androidContext(context)
            modules(listOf(retrofitTestModule, testModule))
        }
    }

    @After
    fun after() {
        stopKoin()
        mockWebServer.shutdown()
    }

    @Test
    fun getAppMode_NoTokenAndNoEmail_ReturnOffline() {
        val appMode = appUserRepo.getAppMode()
        Truth.assertThat(appMode).isEqualTo(AppMode.OFFLINE)
    }

    @Test
    fun loginUser_Success_AppModeLogged() {
        val responseBody =
            LoginResponseDto(authToken = "token", isDataAvailable = false)
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(Gson().toJson(responseBody))
        mockWebServer.enqueue(response)

        val returnedPair = runBlocking {
            appUserRepo.loginUser(email = "test@email.com", password = "abc")
        }
        val apiResponse = returnedPair.first
        val isDataAvailable = returnedPair.second

        Truth.assertThat(apiResponse).isEqualTo(ApiResponse.OK)
        Truth.assertThat(isDataAvailable).isFalse()

        val appMode = appUserRepo.getAppMode()
        Truth.assertThat(appMode).isEqualTo(AppMode.LOGGED)
    }
}