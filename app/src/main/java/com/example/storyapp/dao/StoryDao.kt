package com.example.storyapp.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.response.Story

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story:Story)

    @Query("DELETE FROM story")
    suspend fun deleteStory(): Int

    @Query("SELECT * FROM story")
    fun findStory(): Cursor
}