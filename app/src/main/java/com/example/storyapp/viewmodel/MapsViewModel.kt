package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.RepositoryStory

class MapsViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {
    fun locationStory(loc: Int, token: String) =
        repositoryStory.location(token, loc)
}