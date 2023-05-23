package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.RepositoryStory

class RegisterViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {
    fun register(name: String, email: String, password: String) = repositoryStory.register(name, email, password)
}