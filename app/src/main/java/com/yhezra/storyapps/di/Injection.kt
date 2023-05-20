package com.yhezra.storyapps.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yhezra.storyapps.data.local.preference.UserPreference
import com.yhezra.storyapps.data.local.room.StoryDatabase
import com.yhezra.storyapps.data.remote.repository.StoryRepository
import com.yhezra.storyapps.data.remote.repository.UserRepository
import com.yhezra.storyapps.data.remote.retrofit.ApiConfig

object Injection {

    fun provideUserRepository(dataStore: DataStore<Preferences>): UserRepository {
        val apiService = ApiConfig.getApiService()
        val authPreferences = UserPreference.getInstance(dataStore)
        return UserRepository.getInstance(apiService, authPreferences)
    }

    fun provideStoryRepository(context: Context, dataStore: DataStore<Preferences>): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(dataStore)
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()
        return StoryRepository.getInstance(apiService, userPreference, dao)
    }
}