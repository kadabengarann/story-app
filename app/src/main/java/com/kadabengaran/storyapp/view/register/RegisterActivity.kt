package com.kadabengaran.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kadabengaran.storyapp.MainActivity
import com.kadabengaran.storyapp.ViewModelFactory
import com.kadabengaran.storyapp.databinding.ActivityRegisterBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.RegisterBody
import com.kadabengaran.storyapp.service.model.User
import com.kadabengaran.storyapp.view.PreferenceViewModel
import com.kadabengaran.storyapp.view.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var preferenceViewModel: PreferenceViewModel
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var user: RegisterBody
    private val factory by lazy {
        ViewModelFactory.getInstance(this)
    }
    private val registerViewModel: RegisterViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]

    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = "Masukkan email"
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }
                else -> {
                    user = RegisterBody(
                        name,
                        email,
                        password
                    )
                    register(user)
                }
            }
        }
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun login(loginBody: LoginBody) {
        registerViewModel.login(loginBody).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        saveSession(
                            User(
                                result.data.name,
                                result.data.token,
                                true,
                            )
                        )
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

    private fun register(register: RegisterBody) {
        registerViewModel.register(register).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        login(LoginBody(register.email, register.password))
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showError(result.error)
                    }
                }
            }
        }
    }

    private fun saveSession(user: User) {
        preferenceViewModel.saveSession(user)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.incProgress.progressOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
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

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameText = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameInput =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailText =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailInput =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordText =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val register =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            play(title)
            play(nameText).after(title)
            play(nameText).with(nameInput)
            play(emailText).after(nameText)
            play(emailText).with(emailInput)
            play(passwordText).after(emailText)
            play(passwordText).with(passwordInput)
            play(register).after(passwordText)
            play(login).after(register)
            start()
        }
    }
}