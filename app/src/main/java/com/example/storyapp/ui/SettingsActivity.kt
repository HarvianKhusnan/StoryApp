package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySettingsBinding
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.viewmodel.AuthenticationViewModel
import com.example.storyapp.viewmodel.ViewModelFactory


class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var binding : ActivitySettingsBinding? = null
    private val getBinding get() = binding!!
    private lateinit var serviceAuthentication: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(getBinding.root)

        view()
        viewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun view(){
        with(getBinding){
            logoutSettings.setOnClickListener(this@SettingsActivity)
        }
    }

    private fun viewModel(){
        val preferences = UserPreferences.getInstances(dataStore)
        serviceAuthentication = ViewModelProvider(this, ViewModelFactory(preferences))[AuthenticationViewModel::class.java]
    }

    override fun onClick(p0: View?) {
        when(p0){
            getBinding.logoutSettings -> {
                serviceAuthentication.forLogout()
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> true
        }
    }
}