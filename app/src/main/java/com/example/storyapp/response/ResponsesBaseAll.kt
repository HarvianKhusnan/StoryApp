package com.example.storyapp.response

import com.google.gson.annotations.SerializedName

data class ResponsesBaseAll (
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
)