package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.RepositoryStory

class LoginViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {
    fun login(email : String, password: String) = repositoryStory.login(email, password)
}