package com.example.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storyapp.data.TokenUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences (context: Context){

    private val pref = context.getSharedPreferences(_prefs, Context.MODE_PRIVATE)

    fun setUser(value: TokenUser){
        val edit = pref.edit()
        edit.putString(_token, value.token)
        edit.apply()
    }

    fun userGet(): TokenUser{
        val model = TokenUser()
        model.token = pref.getString(_token, "")
        return  model
    }
    companion object{
        private const val _prefs = "pref_user"
        private const val _token = "token"
    }
}