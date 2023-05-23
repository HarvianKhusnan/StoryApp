package com.example.storyapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenUser (
    var token: String? = ""
): Parcelable