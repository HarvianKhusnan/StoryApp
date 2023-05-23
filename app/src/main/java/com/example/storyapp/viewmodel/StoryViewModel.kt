package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.RepositoryStory
import com.example.storyapp.response.Story

class StoryViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {
    fun stories(token: String): LiveData<PagingData<Story>> =
        repositoryStory.story(token).cachedIn(viewModelScope)
}