package com.yhezra.storyapps.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yhezra.storyapps.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryEntity>)

    @Query("SELECT * FROM Story")
    fun getStories(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM Story")
    fun getListStories(): List<StoryEntity>

    @Query("DELETE FROM Story")
    suspend fun clearStory()
}