package com.example.storyapp.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapp.response.Story

@Database(
    entities = [Story::class],
    version = 2,
    exportSchema = false
)
abstract  class DatabaseStory : RoomDatabase() {
    companion object{
        private var INSTANCE: DatabaseStory? = null

        fun database(context: Context) : DatabaseStory?{
            if(INSTANCE == null){
                synchronized(DatabaseStory::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseStory::class.java,
                        "database_story"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
    abstract fun daoStory(): StoryDao
}