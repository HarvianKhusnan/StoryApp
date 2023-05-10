package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.viewmodel.AuthenticationViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

class SplashScreen : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var binding: ActivitySplashScreenBinding? = null
    private val getBinding get()= binding!!
    private var finish = false
    private lateinit var authenticationService: AuthenticationViewModel
    private lateinit var handler: Handler

    companion object {
        const val DELAY = 2000L
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(getBinding.root)

        supportActionBar?.hide()

        viewModel()

        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            authenticationService.getKeyUser().observe(this){
                if(it.isNullOrEmpty()){
                    startActivity(Intent(this, LoginActivity::class.java))
                }else{
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            finish()
        }, DELAY)
    }

    private fun viewModel(){
        val preferences = UserPreferences.getInstances(dataStore)
        authenticationService = ViewModelProvider(this, ViewModelFactory(preferences))[AuthenticationViewModel::class.java]
    }

    override fun onStop() {
        super.onStop()
        if(finish)
            finish
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}