package com.yhezra.storyapps.data.remote.repository

import android.util.Log
import com.yhezra.storyapps.data.local.preference.UserPreference
import com.yhezra.storyapps.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.yhezra.storyapps.data.remote.utils.story.Result
import com.yhezra.storyapps.data.remote.utils.EspressoIdlingResource
import kotlinx.coroutines.flow.emitAll

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun isLogin(): Flow<String?> = flow { emitAll(userPreference.getToken()) }

    fun login(email: String, password: String): Flow<Result<String>> = flow {
        emit(Result.Loading)
        EspressoIdlingResource.increment()
        try {
            val response = apiService.login(email, password)
            val token = response.loginResult.token
            userPreference.saveToken(token)
            emit(Result.Success(response.message))
            EspressoIdlingResource.decrement()
        } catch (e: Exception) {
            Log.d("UserRepository", "login: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
            EspressoIdlingResource.decrement()
        }
    }

    fun register(name: String, email: String, password: String): Flow<Result<String>> = flow {
        emit(Result.Loading)
        EspressoIdlingResource.increment()
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response.message))
            EspressoIdlingResource.decrement()
        } catch (e: Exception) {
            Log.d("UserRepository", "register: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
            EspressoIdlingResource.decrement()
        }
    }

    fun logout(): Flow<Result<String>> = flow {
        emit(Result.Loading)
        userPreference.logout()
        emit(Result.Success("success"))
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}