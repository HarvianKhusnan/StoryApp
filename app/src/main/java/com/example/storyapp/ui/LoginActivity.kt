package com.example.storyapp.ui


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.session.MediaSession.Token
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.data.TokenUser
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.utils.UserPreferences
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import com.example.storyapp.utils.Result


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPref: UserPreferences
    private var _token: TokenUser = TokenUser()

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.instance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
        playAnimation()
    }

    private fun setup(){
       userPref = UserPreferences(this)
        val user = userPref.userGet()
        if(!user.token.isNullOrBlank()){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener{
            val mail = binding.emailText.text.toString()
            val pw = binding.passwordText.text.toString()
            viewModel.login(
                mail,
                pw
            ).observe(this){result ->
                if(result != null){
                    when (result){
                        is Result.Loading -> {
                            binding.loadingBar.visibility = View.INVISIBLE
                        }
                        is Result.onSuccess -> {
                            binding.loadingBar.visibility = View.GONE
                            Toast.makeText(this, "Login succes on ${result.utils.message}", Toast.LENGTH_SHORT).show()
                            val resp = result.utils
                            token(resp.loginResult.token)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        is Result.onError -> {
                            binding.loadingBar.visibility = View.GONE
                            Toast.makeText(this, "Login ${result.error}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }
    }

    private fun token(token: String){
        _token.token = token
        userPref.setUser(_token)
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.logoView, View.TRANSLATION_X, -20f, 20f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcomeText = ObjectAnimator.ofFloat(binding.welcomeText, View.ALPHA, 1f).setDuration(500)
        val welcomeLogin = ObjectAnimator.ofFloat(binding.welcomeLogin, View.ALPHA, 1f).setDuration(500)
        val emailLayout = ObjectAnimator.ofFloat(binding.textInputLayout, View.ALPHA, 1f).setDuration(500)
        val pwLayout = ObjectAnimator.ofFloat(binding.textInputLayout2, View.ALPHA,1f).setDuration(500)
        val registText = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val registBtn = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(emailLayout, pwLayout, registText, registBtn, welcomeText, welcomeLogin, loginBtn)
            start()
        }

    }


}