package com.yhezra.storyapps.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yhezra.storyapps.data.remote.repository.StoryRepository
import com.yhezra.storyapps.data.remote.response.StoryResponse
import kotlinx.coroutines.launch
import com.yhezra.storyapps.data.Result
import java.io.File

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyResponse = MutableLiveData<Result<StoryResponse>>()
    val storyResponse: LiveData<Result<StoryResponse>>
        get() = _storyResponse

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            storyRepository.getDetailStory(id).collect {
                _storyResponse.value = it
            }
        }
    }

    fun addStory(description: String, file: File) =
        storyRepository.addStory(description, file).asLiveData()
}