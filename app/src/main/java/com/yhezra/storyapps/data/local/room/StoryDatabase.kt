package com.yhezra.storyapps.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yhezra.storyapps.data.local.entity.RemoteKeys
import com.yhezra.storyapps.data.local.entity.StoryEntity
import com.yhezra.storyapps.data.remote.response.StoryResponse

@Database(entities = [StoryEntity::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao

    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        var instance: StoryDatabase? = null
        fun getInstance(context: Context): StoryDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "Story.db"
                ).build()
            }
    }
}