package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.utils.Resource
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.utils.keyboard
import com.example.storyapp.viewmodel.AuthenticationViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name= "user_key")
    private var binding: ActivityLoginBinding? = null
    private val getBinding get() = binding!!
    private lateinit var serviceAuthentication : AuthenticationViewModel
    private var error = false
    private var finish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(getBinding.root)

        view()
        viewModel()
        setEmail()
        setPassword()
        playAnimation()
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(getBinding.logoView, View.TRANSLATION_X, -20f, 20f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcomeText = ObjectAnimator.ofFloat(getBinding.welcomeText, View.ALPHA, 1f).setDuration(500)
        val welcomeLogin = ObjectAnimator.ofFloat(getBinding.welcomeLogin, View.ALPHA, 1f).setDuration(500)
        val emailLayout = ObjectAnimator.ofFloat(getBinding.textInputLayout, View.ALPHA, 1f).setDuration(500)
        val pwLayout = ObjectAnimator.ofFloat(getBinding.textInputLayout2, View.ALPHA,1f).setDuration(500)
        val registText = ObjectAnimator.ofFloat(getBinding.textView, View.ALPHA, 1f).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(getBinding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val registBtn = ObjectAnimator.ofFloat(getBinding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(emailLayout, pwLayout, registText, registBtn, welcomeText, welcomeLogin, loginBtn)
            start()
        }

    }

    private fun view(){
        with(getBinding){
            btnRegister.setOnClickListener(this@LoginActivity)
            btnLogin.setOnClickListener(this@LoginActivity)
        }
    }

    private fun viewModel(){
        val preferences = UserPreferences.getInstances(dataStore)
        serviceAuthentication = ViewModelProvider(this, ViewModelFactory(preferences))[AuthenticationViewModel::class.java]

        serviceAuthentication.authInfo.observe(this){
            when(it){
                is Resource.forSucces -> {
                    loading(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish = true
                }
                is Resource.Loading -> loading(true)
                is Resource.forError -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    loading(false)
                }
            }
        }
    }

    override fun onStop(){
        super.onStop()
        if(finish)
            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClick(p0: View?) {
        when(p0){
            getBinding.btnRegister -> startActivity(Intent(this, RegisterActivity::class.java))
            getBinding.btnLogin-> {
                if(login()){
                    val mail = getBinding.emailText.text.toString()
                    val pw = getBinding.passwordText.text.toString()

                    keyboard(this)
                    serviceAuthentication.getLogin(mail,pw)
                }else{
                    Toast.makeText(this,resources.getString(R.string.wrong_input), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun login() = getBinding.emailText.error == null && getBinding.passwordText.error == null && !getBinding.emailText.text.isNullOrEmpty() && !getBinding.passwordText.text.isNullOrEmpty()

    private fun loading(state: Boolean){
        getBinding.loadingBar.visibility = if(state) View.VISIBLE else View.GONE
        getBinding.btnLogin.isEnabled = !state
    }

    private fun setEmail() {
        getBinding.apply {
            emailText.validateData(
                activity = this@LoginActivity,
                hiddingError = {
                    textInputLayout.isErrorEnabled = false
                    error = false
                },
                setError = {
                    textInputLayout.error = it
                    textInputLayout.isErrorEnabled = true
                    error = true
                }
            )
        }
    }

    private fun setPassword(){
        getBinding.apply {
            passwordText.validateData(
                activity = this@LoginActivity,
                closeMessage = {
                    textInputLayout2.isErrorEnabled = false
                    error = false
                },
                setError = {
                    textInputLayout2.error = it
                    textInputLayout2.isErrorEnabled = true
                    error = true
                }
            )
        }
    }
}