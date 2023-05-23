package com.example.storyapp.ui
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.viewmodel.RegisterViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import com.example.storyapp.utils.Result

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel : RegisterViewModel by viewModels {
        ViewModelFactory.instance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    private fun setup(){
        val name = binding.textName.text.toString()
        val mail = binding.emailEditRegist.text.toString()
        val pw = binding.passwordEditRegist.text.toString()
        binding.loginBtnRegister.setOnClickListener{
            viewModel.register(name,mail,pw).observe(this){ result ->
                if(result != null){
                    when(result){
                        is Result.Loading -> {
                            loading(true)
                        }
                        is Result.onSuccess -> {
                            loading(false)
                            binding.loadingbar.visibility = View.GONE
                            Toast.makeText(this, "Register ${result.utils.message}", Toast.LENGTH_SHORT)

                        }
                        is Result.onError -> {
                            loading(false)
                            binding.loadingbar.visibility = View.GONE
                            Toast.makeText(this, "Register ${result.error}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun loading(loading: Boolean){
        if(loading){
            binding.loadingbar.visibility = View.INVISIBLE
            binding.loginBtnRegister.isEnabled = false
        }else{
            binding.loadingbar.visibility = View.GONE
            binding.loginBtnRegister.isEnabled = true
        }
    }

}