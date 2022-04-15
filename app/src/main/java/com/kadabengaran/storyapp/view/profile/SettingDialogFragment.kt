package com.kadabengaran.storyapp.view.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kadabengaran.storyapp.databinding.FragmentSettingDialogBinding
import com.kadabengaran.storyapp.view.PreferenceViewModel


class SettingDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSettingDialogBinding? = null
    private lateinit var preferenceViewModel: PreferenceViewModel
    private lateinit var switch_id: SwitchCompat

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root
        switch_id = binding.darkModeSwitch

        setupViewModel()
        setupAction()
        return root
    }
    private fun setupViewModel() {
       preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
        preferenceViewModel.getThemeSettings().observe(
            this
        ) { isDarkModeActive: Boolean ->
            switch_id.isChecked = isDarkModeActive
        }
    }
    private fun setupAction(){
        binding.touchLogout.setOnClickListener {
            preferenceViewModel.logout()
            dismiss()
        }
        binding.touchLang.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.darkModeSwitch.setOnCheckedChangeListener{ _: CompoundButton?, isChecked: Boolean ->
            preferenceViewModel.saveThemeSetting(isChecked)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}