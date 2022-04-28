package com.kadabengaran.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.kadabengaran.storyapp.R
import com.kadabengaran.storyapp.databinding.ActivityLoginBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.User
import com.kadabengaran.storyapp.view.MainActivity
import com.kadabengaran.storyapp.view.PreferenceViewModel
import com.kadabengaran.storyapp.view.ViewModelFactory
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
        observe()
        setupAction()
        playAnimation()
    }

    private fun observe() {
        loginViewModel.loginResult.observe(this) { result ->
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
                        finish()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showError(result.error)

                    }
                }
            }
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
        setLoginEnable()
    }

    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
        preferenceViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupAction() {
        val password = binding.passwordInput
        val email = binding.emailInput
        password.doAfterTextChanged { setLoginEnable() }
        email.doAfterTextChanged { setLoginEnable() }

        binding.btnLogin.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            login(LoginBody(emailText, passwordText))
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun setLoginEnable() {
        binding.btnLogin.isEnabled = (binding.passwordInput.check() && binding.emailInput.check())
    }

    private fun login(login: LoginBody) {
        loginViewModel.login(login)
    }

    private fun saveSession(user: User) {
        preferenceViewModel.saveSession(user)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.incProgress.progressOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val emailIcon = ObjectAnimator.ofFloat(binding.emailIcon, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.emailInput, View.ALPHA, 1f).setDuration(500)
        val passwordIcon = ObjectAnimator.ofFloat(binding.passIcon, View.ALPHA, 1f).setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.passwordInput, View.ALPHA, 1f).setDuration(500)
        val separatorLine1 = ObjectAnimator.ofFloat(binding.view, View.ALPHA, 1f).setDuration(500)
        val separatorText =
            ObjectAnimator.ofFloat(binding.tvSeparator, View.ALPHA, 1f).setDuration(500)
        val separatorLine2 = ObjectAnimator.ofFloat(binding.view2, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            play(title)
            play(emailInput).with(emailIcon).after(title)
            play(passwordInput).with(passwordIcon).after(emailInput)
            play(login).after(passwordInput)
            play(separatorText).with(separatorLine1).with(separatorLine2).after(login)
            play(register).after(separatorText)
            start()
        }
    }

    private fun showError(msg: String) {
        AlertDialog.Builder(this, R.style.AlertDialog).apply {
            setTitle(getString(R.string.failed))
            setMessage(msg)
            setNegativeButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

}