package com.yhezra.storyapps.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yhezra.storyapps.data.local.entity.StoryEntity
import com.yhezra.storyapps.data.remote.repository.StoryRepository
import com.yhezra.storyapps.data.remote.repository.UserRepository

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _listStory = MutableLiveData<PagingData<StoryEntity>>()

    val listStory: LiveData<PagingData<StoryEntity>>
        get() = _listStory

    private val observer = Observer<PagingData<StoryEntity>> { _listStory.value = it }

    init {
        getAllStories()
    }

    fun getAllStories() {
        storyRepository.getAllStories().cachedIn(viewModelScope).observeForever(observer)
    }

    fun isLogin() = userRepository.isLogin().asLiveData()

    fun logout() = userRepository.logout().asLiveData()

    override fun onCleared() {
        storyRepository.getAllStories().removeObserver(observer)
        super.onCleared()
    }

}