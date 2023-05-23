package com.example.storyapp.dao

import android.database.Cursor
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.response.Story

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: List<Story>)

    @Query("DELETE FROM story")
    suspend fun deleteStory()

    @Query("SELECT * FROM story")
    fun findStory(): PagingSource<Int, Story>
}