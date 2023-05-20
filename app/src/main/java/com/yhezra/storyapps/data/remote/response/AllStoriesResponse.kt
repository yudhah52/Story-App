package com.yhezra.storyapps.data.remote.response

import com.google.gson.annotations.SerializedName

data class AllStoriesResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStoryResponse: List<StoryResponse>
)
