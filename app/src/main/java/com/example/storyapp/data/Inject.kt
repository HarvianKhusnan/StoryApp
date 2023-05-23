package com.example.storyapp.data

import android.content.Context
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.dao.DatabaseStory

object Inject {
    fun repositoryProvider(context: Context) : RepositoryStory {
        val db = DatabaseStory.getStory(context)
        val apiService = ApiConfig.ApiService()
        return RepositoryStory(apiService, db)
    }
}