package com.example.storyapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.dao.DatabaseStory
import com.example.storyapp.dao.StoryDao
import com.example.storyapp.response.ResponsesBaseAll
import com.example.storyapp.response.Story
import com.example.storyapp.response.StoryResponse
import com.example.storyapp.utils.Resource
import com.example.storyapp.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val preferences: UserPreferences, application: Application) : AndroidViewModel(application)
{
    private var daoStory: StoryDao? = null
    private val storyList = MutableLiveData<ArrayList<Story>>()
    private var databaseStory: DatabaseStory? = DatabaseStory.database(application)
    private val storiesData = MutableLiveData<Resource<ArrayList<Story>>>()
    val story : LiveData<Resource<ArrayList<Story>>> = storiesData

    init {
        daoStory = databaseStory?.daoStory()
    }

    suspend fun stories(){
        storiesData.postValue(Resource.Loading())
        val clientService = ApiConfig.apiInstance.getForStories(token = "Bearer ${preferences.getKey().first()}")

        clientService.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        val listStory = it.storyList

                        viewModelScope.launch {
                            daoStory?.deleteStory()
                            listStory.forEach {  story ->
                                daoStory?.insert(story)
                            }
                        }
                        storiesData.postValue(Resource.forSucces(ArrayList(listStory)))
                    }
                }else{
                    val errorMsg = Gson().fromJson(response.errorBody()?.charStream(),ResponsesBaseAll::class.java)
                    storiesData.postValue(Resource.forError(errorMsg.message))
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(MainViewModel::class.java.simpleName, "Gagal mengambil Story")
                storiesData.postValue(Resource.forError(t.message))
            }
        })
    }

    fun getStory() : MutableLiveData<ArrayList<Story>>{
        return storyList
    }

}