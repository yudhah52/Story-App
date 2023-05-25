package com.yhezra.storyapps.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yhezra.storyapps.data.remote.repository.StoryRepository
import com.yhezra.storyapps.data.remote.response.StoryResponse
import com.yhezra.storyapps.data.remote.utils.story.Result
import kotlinx.coroutines.launch
import java.io.File

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<Result<StoryResponse>>()
    val story: LiveData<Result<StoryResponse>>
        get() = _story

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            storyRepository.getDetailStory(id).collect {
                _story.value = it
            }
        }
    }

    fun addStory(description: String, file: File, lat: Double?, lon: Double?) =
        storyRepository.addStory(description, file, lat, lon).asLiveData()
}