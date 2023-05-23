package com.example.storyapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.ApiService
import com.example.storyapp.data.Inject
import com.example.storyapp.data.RepositoryStory
import com.example.storyapp.utils.UserPreferences


class ViewModelFactory private constructor(private val story: RepositoryStory ) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(story) as T
        }
        if(modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(story) as T
        }
        if(modelClass.isAssignableFrom(StoryAddViewModel::class.java)){
            return StoryAddViewModel(story) as T
        }
        if(modelClass.isAssignableFrom(MapsViewModel::class.java)){
            return MapsViewModel(story) as T
        }
        if(modelClass.isAssignableFrom(StoryViewModel::class.java)){
            return StoryViewModel(story) as T
        }
        throw IllegalAccessException("Class ViewModel Tidak Diketahui" + modelClass.name)
    }

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null
        fun instance(context: Context): ViewModelFactory{
            return instance ?: synchronized(this){
                instance?: ViewModelFactory(Inject.repositoryProvider(context))
            }.also { instance =it }
        }
    }
}