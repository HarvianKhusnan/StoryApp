package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import com.example.storyapp.R
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.utils.Resource
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.utils.keyboard
import com.example.storyapp.viewmodel.AuthenticationViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name ="user_key")
    private var binding: ActivityRegisterBinding? = null
    private val getBinding get() = binding!!
    private var error = false
    private lateinit var serviceAuth: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(getBinding.root)

        view()
        setEmail()
        setPassword()
        viewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClick(p0: View?) {
        when(p0){
            getBinding.backButton -> finish()
            getBinding.loginBtnRegister -> {
                val nama = getBinding.textName.text.toString()
                val mail = getBinding.emailEditRegist.text.toString()
                val pw = getBinding.passwordEditRegist.text.toString()

                if(getBinding.emailEditRegist.error == null && getBinding.passwordEditRegist.error == null){
                    keyboard(this)
                    serviceAuth.getRegister(nama,mail,pw)
                }
            }
        }
    }

    private fun view(){
        with(getBinding){
            backButton.setOnClickListener(this@RegisterActivity)
            loginBtnRegister.setOnClickListener(this@RegisterActivity)
        }
    }

    private fun viewModel() {
        val preferences = UserPreferences.getInstances(dataStore)
        serviceAuth = ViewModelProvider(this, ViewModelFactory(preferences))[AuthenticationViewModel::class.java]

        serviceAuth.authInfo.observe(this){
            when(it){
                is Resource.forSucces -> {
                    loading(false)
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                is Resource.Loading -> loading(true)
                is Resource.forError -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    loading(false)
                }
            }
        }
    }

    private fun loading(isLoading: Boolean){
        getBinding.loadingbar.visibility = if(isLoading) View.VISIBLE else View.GONE
        getBinding.loginBtnRegister.isEnabled = !isLoading
    }

    private fun setEmail() {
        getBinding.apply {
            emailEditRegist.validateData(
                activity = this@RegisterActivity,
                hiddingError = {
                    textInputLayout4.isErrorEnabled = false
                    error = false
                },
                setError = {
                    textInputLayout4.error = it
                    textInputLayout4.isErrorEnabled = true
                    error = true
                }
            )
        }
    }

    private fun setPassword(){
        getBinding.apply {
            passwordEditRegist.validateData(
                activity = this@RegisterActivity,
                closeMessage = {
                    textInputLayout5.isErrorEnabled = false
                    error = false
                },
                setError = {
                    textInputLayout5.error = it
                    textInputLayout5.isErrorEnabled = true
                    error = true
                }
            )
        }
    }

}