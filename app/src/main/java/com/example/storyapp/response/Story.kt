package com.example.storyapp.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
data class Story(
    @PrimaryKey
    @SerializedName("id")
    val id:String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val descript: String,

    @SerializedName("photoUrl")
    val photoUrl: String,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,
)
