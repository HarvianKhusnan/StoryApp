package com.example.storyapp.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keys_remote")
data class RemoteKeysStory(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey : Int?
)
