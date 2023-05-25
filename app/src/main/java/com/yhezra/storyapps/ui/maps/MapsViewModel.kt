package com.yhezra.storyapps.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yhezra.storyapps.data.remote.repository.StoryRepository
import com.yhezra.storyapps.data.remote.response.StoryResponse
import com.yhezra.storyapps.data.remote.utils.story.Result
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<Result<List<StoryResponse>>>()
    val story: LiveData<Result<List<StoryResponse>>>
        get() = _story

    init {
        getAllStoriesWithLocation()
    }

    private fun getAllStoriesWithLocation() {
        viewModelScope.launch {
            storyRepository.getAllStoriesWithLocation().collect {
                _story.value = it
            }
        }
    }
}