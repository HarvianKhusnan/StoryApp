package com.example.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.response.LoginResponse
import com.example.storyapp.response.RequestLogin
import com.example.storyapp.response.RequestRegister
import com.example.storyapp.response.ResponsesBaseAll
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthenticationViewModel(private val pref: UserPreferences) : ViewModel(){
    private val infoForAuth = MutableLiveData<Resource<String>>()
    val authInfo : LiveData<Resource<String>> = infoForAuth

    fun getLogin(email: String, pw : String){
        infoForAuth.postValue(Resource.Loading())
        val clientService = ApiConfig.apiInstance.loginUser(RequestLogin(email,pw))

        clientService.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    val resultLogin = response.body()?.loginResult?.token
                    resultLogin?.let {keySaveUser(it)}
                    infoForAuth.postValue(Resource.forSucces(resultLogin))
                }else{
                    val responseError = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ResponsesBaseAll::class.java
                    )
                    infoForAuth.postValue(Resource.forError(responseError.message))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(AuthenticationViewModel::class.java.simpleName, "Gagal Login")
                infoForAuth.postValue(Resource.forError(t.message))
            }
        })
    }

    fun getRegister(name: String, email: String, password:String){
        infoForAuth.postValue(Resource.Loading())
        val clientResponse = ApiConfig.apiInstance.registerUser(RequestRegister(name,email, password))

        clientResponse.enqueue(object : Callback<ResponsesBaseAll>{
            override fun onResponse(
                call: Call<ResponsesBaseAll>,
                response: Response<ResponsesBaseAll>
            ) {
                if(response.isSuccessful){
                    val msg = response.body()?.message.toString()
                    infoForAuth.postValue(Resource.forSucces(msg))
                }else{
                    val errorMsg = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ResponsesBaseAll::class.java
                    )
                    infoForAuth.postValue(Resource.forError(errorMsg.message))
                }
            }

            override fun onFailure(call: Call<ResponsesBaseAll>, t: Throwable) {
                Log.e(AuthenticationViewModel::class.java.simpleName, "Gagal Register")
                infoForAuth.postValue(Resource.forError(t.message))
            }
        })
    }

    fun forLogout() {
        deleteKeyUser()
    }

    fun getKeyUser() = pref.getKey().asLiveData()


    private fun keySaveUser(key: String){
        viewModelScope.launch {
            pref.saveKey(key)
        }
    }

    fun deleteKeyUser(){
        viewModelScope.launch {
            pref.deleteKey()
        }
    }
}