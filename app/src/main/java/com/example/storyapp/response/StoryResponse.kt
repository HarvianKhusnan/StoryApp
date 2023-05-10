package com.example.storyapp.response

import android.os.Message
import com.google.gson.annotations.SerializedName

data class StoryResponse (
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("listStory")
    val storyList: List<Story>,
)