package com.example.storyapp.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){

    private val keyUser = stringPreferencesKey("user_key")
    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstances(dataStore: DataStore<Preferences>): UserPreferences{
            return INSTANCE ?: synchronized(this){
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getKey(): Flow<String>{
        return dataStore.data.map { pref ->
            pref[keyUser] ?: ""
        }
    }

    suspend fun saveKey(key: String){
        dataStore.edit { pref ->
            pref[keyUser] = key
        }
    }
    suspend fun deleteKey(){
        dataStore.edit { pref ->
            pref.remove(keyUser)
        }
    }



}