package com.yhezra.storyapps.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Story")
data class StoryEntity(
    @field:ColumnInfo(name = "Id")
    @field:PrimaryKey
    val id: String,

    @field:ColumnInfo(name = "Name")
    val name: String,

    @field:ColumnInfo(name = "Description")
    val description: String,

    @field:ColumnInfo(name = "PhotoUrl")
    val photoUrl: String,

    @field:ColumnInfo("CreatedAt")
    val createdAt: String,

    @field:ColumnInfo(name = "Lat")
    val lat: Double,

    @field:ColumnInfo(name = "Lon")
    val lon: Double
)