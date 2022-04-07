package com.kadabengaran.storyapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kadabengaran.storyapp.databinding.ActivityMainBinding
import com.kadabengaran.storyapp.view.PreferenceViewModel
import com.kadabengaran.storyapp.view.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceViewModel: PreferenceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
        setupViewModel()
        setupView()
        setupAction()
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
    private fun showContent(auth: Boolean){
            binding.incProgress.progressOverlay.visibility = if (!auth) View.VISIBLE else View.GONE
        }
    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
        preferenceViewModel.getUser().observe(this) { user ->
            if (!user.isLogin){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                binding.nameTextView.text = getString(R.string.greeting, user.name)
                Log.d(TAG, "setupViewModel: ${user.token}")
            }
            showContent(user.isLogin)
        }
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            preferenceViewModel.logout()
        }
    }

}