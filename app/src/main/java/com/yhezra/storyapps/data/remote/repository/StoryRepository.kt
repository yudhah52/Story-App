package com.yhezra.storyapps.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.yhezra.storyapps.data.remote.response.StoryResponse
import com.yhezra.storyapps.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.yhezra.storyapps.data.remote.utils.story.Result
import com.yhezra.storyapps.data.local.entity.StoryEntity
import com.yhezra.storyapps.data.local.preference.UserPreference
import com.yhezra.storyapps.data.local.room.StoryDatabase
import com.yhezra.storyapps.data.remote.utils.reduceFileImage
import com.yhezra.storyapps.data.remote.utils.EspressoIdlingResource
import com.yhezra.storyapps.data.remote.utils.story.RemoteMediator
import okhttp3.RequestBody.Companion.asRequestBody

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(): LiveData<PagingData<StoryEntity>> = liveData {
        EspressoIdlingResource.increment()
        userPreference.getToken().collect {
            if (it != null) {
                val response = Pager(
                    config = PagingConfig(pageSize = 5),
                    remoteMediator = RemoteMediator(storyDatabase, apiService, it),
                    pagingSourceFactory = { storyDatabase.storyDao().getStories() },
                ).liveData
                emitSource(response)
                EspressoIdlingResource.decrement()
            }
        }
    }

    fun getAllStoriesWithLocation(): Flow<Result<List<StoryResponse>>> = flow {
        emit(Result.Loading)
        EspressoIdlingResource.increment()
        try {
            userPreference.getToken().collect {
                if (it != null) {
                    val response = apiService.getAllStoriesWithLocation(it)
                    emit(Result.Success(response.listStoryResponse))
                    EspressoIdlingResource.decrement()
                }
            }
        } catch (e: Exception) {
            Log.d("StoryRepository", "getAllStoriesWithLocation: ${e.message.toString()}")
//            emit(StoryResult.Error(e.message.toString()))
            EspressoIdlingResource.decrement()
        }
    }

    fun getDetailStory(id: String): Flow<Result<StoryResponse>> = flow {
        emit(Result.Loading)
        EspressoIdlingResource.increment()
        try {
            userPreference.getToken().collect {
                if (it != null) {
                    val response = apiService.getDetailStory(it, id)
                    emit(Result.Success(response.storyResponse))
                    EspressoIdlingResource.decrement()
                }
            }
        } catch (e: Exception) {
            Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
            EspressoIdlingResource.decrement()
        }
    }

    fun addStory(
        description: String,
        file: File,
        lat: Double?,
        lon: Double?
    ): Flow<Result<String>> = flow {
        emit(Result.Loading)
        EspressoIdlingResource.increment()
        val reducedFile = reduceFileImage(file)
        val desc = description.toRequestBody("text/plain".toMediaType())
        val latitude = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val longitude = lon?.toString()?.toRequestBody("text/plain".toMediaType())
        val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
        try {
            userPreference.getToken().collect {
                if (it != null) {
                    val response =
                        apiService.addStory(it, imageMultipart, desc, latitude, longitude)
                    emit(Result.Success(response.message))
                    EspressoIdlingResource.decrement()
                }
            }
        } catch (e: Exception) {
            Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
            EspressoIdlingResource.decrement()
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            authPreferences: UserPreference,
            storyDatabase: StoryDatabase,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, authPreferences, storyDatabase)
            }.also { instance = it }
    }
}