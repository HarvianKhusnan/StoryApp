package com.example.storyapp.utils

sealed class Result<out R> private constructor(){
    data class onSuccess<out T>(val utils: T) : Result<T>()
    data class onError(val error: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
