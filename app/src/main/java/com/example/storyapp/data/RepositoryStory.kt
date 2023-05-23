package com.example.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.api.ApiService
import com.example.storyapp.dao.DatabaseStory
import com.example.storyapp.response.LoginResponse
import com.example.storyapp.response.ResponsesBaseAll
import com.example.storyapp.response.Story
import com.example.storyapp.response.StoryResponse
import com.example.storyapp.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody


class RepositoryStory(private val apiService: ApiService, private val databaseStory: DatabaseStory) {

    fun story(token : String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemote(databaseStory, apiService, token),
            pagingSourceFactory = {
                databaseStory.daoStories().findStory()
            }
        ).liveData
    }

    fun register(name: String, email: String, password: String): LiveData<Result<ResponsesBaseAll>> = liveData {
        emit(Result.Loading)
        try{
            val resp = apiService.registerUser(name, email, password)
            emit(Result.onSuccess(resp))
        }catch (e : Exception){
            emit(Result.onError(e.message.toString()))
        }
    }
    fun location(token: String, loc: Int): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.locationAll("Bearer $token", loc)
            emit(Result.onSuccess(response))
        } catch (e: Exception) {
            emit(Result.onError(e.message.toString()))
        }
    }

    fun login(email: String, password: String) : LiveData<Result<LoginResponse>> = liveData{
        emit(Result.Loading)
        try{
            val resp = apiService.loginUser(email, password)
            emit(Result.onSuccess(resp))
        }catch (e: Exception){
            emit(Result.onError(e.message.toString()))
        }
    }

    fun imageUpload(
        file: MultipartBody.Part,
        descript : RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null,
        token: String
    ): LiveData<Result<ResponsesBaseAll>> = liveData {
        emit(Result.Loading)
        try{
            val resp = apiService.addStory("Bearer $token",file,descript,lat,lon)
            emit(Result.onSuccess(resp))
        }catch (e: Exception){
            Log.d(TAG, "Terjadi kesalahan dalam mengupload gambar : ${e.message.toString()}")
            emit(Result.onError(e.message.toString()))
        }
    }

    companion object{
       private const val TAG = "RepositoryStory"
    }

}

