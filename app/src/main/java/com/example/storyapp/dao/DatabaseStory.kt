package com.example.storyapp.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapp.data.StoryRemote
import com.example.storyapp.response.Story

@Database(
    entities = [Story::class, RemoteKeysStory::class],
    version = 1,
    exportSchema = false
)
abstract  class DatabaseStory : RoomDatabase() {
    abstract fun daoStories(): StoryDao
    abstract fun keysRemoteDao(): RemoteKeysDao
    companion object{
        @Volatile
        private var INSTANCE: DatabaseStory? = null
        @JvmStatic
        fun getStory(context: Context): DatabaseStory{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseStory::class.java, "database_story"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it}
            }
        }
    }
}