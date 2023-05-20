package com.yhezra.storyapps.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yhezra.storyapps.data.remote.repository.StoryRepository
import com.yhezra.storyapps.data.remote.repository.UserRepository
import com.yhezra.storyapps.data.remote.response.StoryResponse
import kotlinx.coroutines.launch
import com.yhezra.storyapps.data.Result

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _listStoryResponse = MutableLiveData<Result<List<StoryResponse>>>()
    val listStoryResponse : LiveData<Result<List<StoryResponse>>>
        get() = _listStoryResponse

    init {
        getAllStories()
    }

    fun getAllStories(){
        viewModelScope.launch {
            storyRepository.getAllStories().collect{
                _listStoryResponse.value = it
            }
        }
    }

    fun isLogin() = userRepository.isLogin().asLiveData()

    fun logout() = userRepository.logout().asLiveData()

}