package com.example.storyapp.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("loginResult")
    val loginResult: Login,
)