package com.kadabengaran.storyapp.view.register

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
import com.kadabengaran.storyapp.view.ViewModelFactory
import com.kadabengaran.storyapp.databinding.ActivityRegisterBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.RegisterBody
import com.kadabengaran.storyapp.service.model.User
import com.kadabengaran.storyapp.view.MainActivity
import com.kadabengaran.storyapp.view.PreferenceViewModel
import com.kadabengaran.storyapp.view.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var preferenceViewModel: PreferenceViewModel
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var user: RegisterBody
    private val factory by lazy {
        ViewModelFactory.getInstance()
    }
    private val registerViewModel: RegisterViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        setRegisterEnable()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
    }

    private fun setupAction() {
        val name = binding.nameInput
        val email = binding.emailInput
        val password = binding.passwordInput

        name.doAfterTextChanged { setRegisterEnable() }
        password.doAfterTextChanged { setRegisterEnable() }
        email.doAfterTextChanged { setRegisterEnable() }

        binding.btnRegister.setOnClickListener {
            val nameText = name.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
                    user = RegisterBody(
                        nameText,
                        emailText,
                        passwordText
                    )
                    register(user)
        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    private fun setRegisterEnable() {
        binding.btnRegister.isEnabled = (binding.passwordInput.check() && binding.emailInput.check() && !binding.nameInput.text.isNullOrEmpty() )
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
        AlertDialog.Builder(this,R.style.AlertDialog).apply {
            setTitle(getString(R.string.failed))
            setMessage(msg)
            setNegativeButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameIcon = ObjectAnimator.ofFloat(binding.nameIcon, View.ALPHA, 1f).setDuration(500)
        val nameInput = ObjectAnimator.ofFloat(binding.nameInput, View.ALPHA, 1f).setDuration(500)
        val emailIcon = ObjectAnimator.ofFloat(binding.emailIcon, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.emailInput, View.ALPHA, 1f).setDuration(500)
        val passwordIcon = ObjectAnimator.ofFloat(binding.passIcon, View.ALPHA, 1f).setDuration(500)
        val passwordInput = ObjectAnimator.ofFloat(binding.passwordInput, View.ALPHA, 1f).setDuration(500)
        val separatorLine1 = ObjectAnimator.ofFloat(binding.view, View.ALPHA, 1f).setDuration(500)
        val separatorText = ObjectAnimator.ofFloat(binding.tvSeparator, View.ALPHA, 1f).setDuration(500)
        val separatorLine2 = ObjectAnimator.ofFloat(binding.view2, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            play(title)
            play(nameInput).with(nameIcon).after(title)
            play(emailInput).with(emailIcon).after(nameInput)
            play(passwordInput).with(passwordIcon).after(emailInput)
            play(register).after(passwordInput)
            play(separatorText).with(separatorLine1).with(separatorLine2).after(register)
            play(login).after(separatorText)
            start()
        }
    }
}