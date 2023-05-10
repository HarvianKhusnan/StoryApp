package com.example.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.response.ResponsesBaseAll
import com.example.storyapp.utils.Resource
import com.example.storyapp.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryAddViewModel(private val preferences: UserPreferences) : ViewModel() {
    private val infoForUpload = MutableLiveData<Resource<String>>()
    val uploadInfo: LiveData<Resource<String>> = infoForUpload

    suspend fun forUpload(
        imageMultipart : MultipartBody.Part,
        description: RequestBody,
        asGuest: Boolean = false
    ){
        infoForUpload.postValue(Resource.Loading())
        val clientService = if(asGuest) ApiConfig.apiInstance.addStoryForGuest(
            imageMultipart, description
        )else ApiConfig.apiInstance.addStory(
            token = "Bearer ${preferences.getKey().first()}",
            imageMultipart,
            description
        )

        clientService.enqueue(object : Callback<ResponsesBaseAll>{
            override fun onResponse(
                call: Call<ResponsesBaseAll>,
                response: Response<ResponsesBaseAll>
            ) {
                if(response.isSuccessful){
                    infoForUpload.postValue(Resource.forSucces(response.body()?.message))
                }else{
                    val msgError = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ResponsesBaseAll::class.java
                    )
                    infoForUpload.postValue(Resource.forError(msgError.message))
                }
            }

            override fun onFailure(call: Call<ResponsesBaseAll>, t: Throwable) {
                Log.e(StoryAddViewModel::class.java.simpleName, "Gagal Upload")
                infoForUpload.postValue(Resource.forError(t.message))
            }
        })
    }

}