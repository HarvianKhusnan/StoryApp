package com.example.storyapp.api

import com.example.storyapp.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") mail : String,
        @Field("password") pw : String,
    ) : ResponsesBaseAll
    @FormUrlEncoded
    @POST("login")
   suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") pw : String
    ) : LoginResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody?,
        @Part("lon") longitude: RequestBody?
    ): ResponsesBaseAll

    @Multipart
    @POST("stories/guest")
   suspend fun addStoryForGuest(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): ResponsesBaseAll

   @GET("stories")
   fun locationAll (
       @Query("location") location: String,
       @Query("page") page: Int,
   ): StoryResponse

    @GET("stories")
   suspend fun getForStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int? = null,
    ): StoryResponse
}