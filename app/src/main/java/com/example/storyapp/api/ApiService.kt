package com.example.storyapp.api

import com.example.storyapp.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun registerUser(@Body request: RequestRegister) : Call<ResponsesBaseAll>

    @POST("login")
    fun loginUser(@Body requestLogin: RequestLogin) : Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<ResponsesBaseAll>

    @Multipart
    @POST("stories/guest")
    fun addStoryForGuest(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<ResponsesBaseAll>

    @GET("stories")
    fun getForStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Call<StoryResponse>
}