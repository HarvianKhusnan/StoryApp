package com.example.storyapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.response.Story

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keyRemote: List<RemoteKeysStory>)

    @Query("SELECT * FROM keys_remote WHERE id = :id")
    suspend fun idKeysRemote(id: String): RemoteKeysStory?

    @Query("DELETE FROM keys_remote")
    suspend fun remoteKeysDelete()
}