package com.yhezra.storyapps.ui.story

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yhezra.storyapps.data.remote.repository.StoryRepository
import com.yhezra.storyapps.di.Injection

class StoryViewModelFactory(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: StoryViewModelFactory? = null
        fun getInstance(
            context: Context,
            dataStore: DataStore<Preferences>
        ): StoryViewModelFactory = instance ?: synchronized(this) {
            instance ?: StoryViewModelFactory(Injection.provideStoryRepository(context, dataStore))
        }.also { instance = it }
    }
}