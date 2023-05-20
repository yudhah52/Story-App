package com.yhezra.storyapps.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yhezra.storyapps.data.remote.response.StoryResponse

@Dao
interface StoryDao {
    @Query("SELECT * FROM Story")
    fun getStories(): List<StoryResponse>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStory(storyResponse: StoryResponse): Long

    @Query("DELETE FROM Story")
    suspend fun clearStory()
}