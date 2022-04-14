package com.kadabengaran.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.kadabengaran.storyapp.MainActivityOld
import com.kadabengaran.storyapp.ViewModelFactory
import com.kadabengaran.storyapp.databinding.ActivityLoginBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.User
import com.kadabengaran.storyapp.view.MainActivity
import com.kadabengaran.storyapp.view.PreferenceViewModel
import com.kadabengaran.storyapp.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var preferenceViewModel: PreferenceViewModel

    private lateinit var binding: ActivityLoginBinding

    private val factory by lazy {
        ViewModelFactory.getInstance(this)
    }
    private val loginViewModel: LoginViewModel by viewModels {
        factory
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }*/
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }

                else -> {
                    login(LoginBody( email, password))
                }
            }
        }
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun login(login: LoginBody){
        loginViewModel.login(login).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        saveSession(User(
                            result.data.name,
                            result.data.token,
                            true,
                        ))
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showError(result.error)

                    }
                }
            }
        }
    }
    private fun saveSession(user: User){
        preferenceViewModel.saveSession(user)
    }
    private fun showLoading(isLoading: Boolean) {
        binding.incProgress.progressOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val titleMessage = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailText = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordText = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordInput = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            play(title).before(titleMessage)
            play(emailText).after(titleMessage)
            play(emailText).with(emailInput)
            play(passwordText).after(emailText)
            play(passwordText).with(passwordInput)
            play(login).after(passwordText)
            play(register).after(login)
            start()
        }
    }
    private fun showError(msg: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Failed!")
            setMessage(msg)
            setNegativeButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

}