package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.RepositoryStory
import com.example.storyapp.utils.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryAddViewModel(private val RepositoryStory : RepositoryStory) : ViewModel() {
    fun imageUpload(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null,
        token: String
    ) = RepositoryStory.imageUpload(file,description, lat, lon,token)

}