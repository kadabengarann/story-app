package com.kadabengaran.storyapp.view.mapStory

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.kadabengaran.storyapp.R
import com.kadabengaran.storyapp.databinding.FragmentStoryMapsBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.model.StoryItem
import com.kadabengaran.storyapp.view.PreferenceViewModel
import com.kadabengaran.storyapp.view.ViewModelFactory

class StoryMapsFragment : Fragment() {

    private var _binding: FragmentStoryMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferenceViewModel: PreferenceViewModel

    private val factory by lazy {
        ViewModelFactory.getInstance(requireContext())
    }
    private val storyMapViewModel: StoryMapViewModel by viewModels {
        factory
    }
    private lateinit var storyMap: GoogleMap
    private var boundsBuilder = LatLngBounds.Builder()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }else{
                showError(getString(R.string.permission_failed))
            }
        }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        storyMap = googleMap
        setMapStyle()
        observeData()
        getMyLocation()
        storyMap.uiSettings.isZoomControlsEnabled = true
        storyMap.uiSettings.isIndoorLevelPickerEnabled = true
        storyMap.uiSettings.isCompassEnabled = true
        storyMap.uiSettings.isMapToolbarEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        _binding = FragmentStoryMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        setupViewModel()

    }

    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
        preferenceViewModel.getUser().observe(viewLifecycleOwner) { user ->
            storyMapViewModel.setToken(user.token)
        }
        if (!storyMapViewModel.fetched) {
            storyMapViewModel.tokenSession.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty())
                    storyMapViewModel.getStories()
            }
        }
        Log.d(TAG, "setupViewModel: ${storyMapViewModel.fetched}")
    }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun setMapStyle() {
        try {
            val success =
                storyMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) ) {
            storyMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun observeData() {
        storyMapViewModel.listStory.observe(viewLifecycleOwner) {
            processData(it)
        }
    }
    private fun processData(result: Result<List<StoryItem>>) {
        if (result != null) {
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val storyList = result.data
                    setStories(storyList)
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(result.error)
                }
            }
        }
    }

    private fun setStories(storyList: List<StoryItem>) {
        if (storyList.isEmpty()) {
            Log.d(TAG, "setStories : NO DATA")
        }
        for (user in storyList) {
            Log.d(TAG, "onLocationResult: " + user.lat + ", " + user.lon)
            showMarker(user)
        }
    }

    private fun showMarker(user: StoryItem) {
        val userLocation = LatLng(user.lat, user.lon)
        storyMap.addMarker(
            MarkerOptions()
                .position(userLocation)
                .title(user.name)
        )
        boundsBuilder.include(userLocation)
        val bounds: LatLngBounds = boundsBuilder.build()
        storyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17f))
        storyMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))


    }

    private fun showError(error: String) {
        error.let {
            binding.incError.tvError.text = it
        }
        binding.incError.grError.visibility = View.VISIBLE
        binding.incError.btnError.setOnClickListener {
            binding.incError.grError.visibility = View.GONE
            storyMapViewModel.getStories()
        }
        Toast.makeText(
            requireContext(),
            getString(R.string.error_string),
            Toast.LENGTH_SHORT
        ).show()
        Log.d(TAG, "showError: $error")
    }

    private fun showLoading(b: Boolean) {
        Log.d(TAG, "setStories: LOADING.....")
        binding.incProgress.progressOverlay.visibility = if (b) View.VISIBLE else View.GONE
    }
}