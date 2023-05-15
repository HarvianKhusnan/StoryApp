package com.example.storyapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.RepositoryStory
import com.example.storyapp.utils.UserPreferences


class ViewModelFactory(private val preferences: UserPreferences, private val storyRepo : RepositoryStory? = null ) : ViewModelProvider.NewInstanceFactory()
{
    private lateinit var appM: Application

    fun setApp(app: Application){
        appM = app
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthenticationViewModel::class.java)){
            return AuthenticationViewModel(preferences) as T
        }
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(preferences, appM, storyRepo!!) as T
        }
        if(modelClass.isAssignableFrom(StoryAddViewModel::class.java)){
            return StoryAddViewModel(preferences) as T
        }
        throw IllegalAccessException("Class ViewModel Tidak Diketahui" + modelClass.name)
    }
}