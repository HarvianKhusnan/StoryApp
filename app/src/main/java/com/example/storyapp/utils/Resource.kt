package com.example.storyapp.utils

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class forSucces<T>(data: T?) : Resource<T>(data)
    class forError<T>(message: String?, data: T? = null): Resource<T>(data, message)
    class Loading<T>(data: T? = null): Resource<T>(data)
}