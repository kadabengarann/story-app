package com.kadabengaran.storyapp.view.post

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.kadabengaran.storyapp.R
import com.kadabengaran.storyapp.components.MyActionButton
import com.kadabengaran.storyapp.databinding.FragmentPostStoryBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.utils.createTempFile
import com.kadabengaran.storyapp.utils.reduceFileImage
import com.kadabengaran.storyapp.utils.uriToFile
import com.kadabengaran.storyapp.view.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class PostStoryFragment : Fragment() {

    private var _binding: FragmentPostStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var storyLocation: Location

    private val factory by lazy {
        ViewModelFactory.getInstance(requireContext())
    }
    private val postStoryViewModel: PostStoryViewModel by viewModels {
        factory
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var captionInput: EditText
    private lateinit var uploadBtn: MyActionButton
    private lateinit var cbLocation: CheckBox
    private val cancellationTokenSource = CancellationTokenSource()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            val isCamera = permissions.entries.all {
                it.key == Manifest.permission.CAMERA
            }
            if (granted) {
                if (isCamera) startTakePhoto() else getMyLastLocation()
            } else {
                // if location action
                if (!isCamera) binding.cbLocation.isChecked = false
                showError(getString(R.string.permission_failed))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        _binding = FragmentPostStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupAction()
        observeView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancellationTokenSource.cancel()
        _binding = null
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupView() {
        captionInput = binding.inpDescription
        uploadBtn = binding.btnUpload
        cbLocation = binding.cbLocation
        setUploadEnable()
    }

    private fun setupAction() {
        cbLocation.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                getMyLastLocation()
            }
        }
        binding.btnCamera.setOnClickListener {
            if (checkPermission(Manifest.permission.CAMERA)) {
                startTakePhoto()
            } else {
                requestPermissionLauncher.launch(
                    CAMERA_PERMISSIONS
                )
            }
        }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }
        captionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setUploadEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun observeView() {
        postStoryViewModel.uploadResult.observe(viewLifecycleOwner) { response ->
            if (response != null) when (response) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.success_upload_story),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    navigateToHome()
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(response.error)
                }
            }
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!gpsEnabled){
                gpsDisabledDialog(requireContext())
                cbLocation.isChecked = false
            }
            else {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? -> //if lastKnown not Null
                    if (location != null) {
                        storyLocation = location
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.location_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        if (!cbLocation.isChecked)
                            cbLocation.isChecked = true
                    }else getMyCurrentLocation() // if null get currentLocation
                }
            }
        } else {
            requestPermissionLauncher.launch(
                LOCATION_PERMISSIONS
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyCurrentLocation() {
        showLoading(true)
        Toast.makeText(
            requireContext(),
            getString(R.string.getting_current_location),
            Toast.LENGTH_LONG
        ).show()
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            showLoading(false)
            if (location != null) {
                storyLocation = location
                Toast.makeText(
                    requireContext(),
                    getString(R.string.location_success),
                    Toast.LENGTH_SHORT
                ).show()
                if (!cbLocation.isChecked)
                    cbLocation.isChecked = true
            } else {
                val snack =
                    Snackbar.make(
                        binding.root,
                        getString(R.string.get_location_failed),
                        Snackbar.LENGTH_SHORT
                    )
                snack.setAction(getString(R.string.btn_retry_string)) { getMyLastLocation() }
                snack.show()
                cbLocation.isChecked = false
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
            setUploadEnable()

        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.let {
            intent.resolveActivity(it.packageManager)
        }
        createTempFile(requireContext()).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.kadabengaran.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImageView.setImageBitmap(result)
            setUploadEnable()
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = captionInput.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            if (cbLocation.isChecked) {
                val lat = storyLocation.latitude.toString().toRequestBody("text/plain".toMediaType())
                val lon = storyLocation.longitude.toString().toRequestBody("text/plain".toMediaType())

                postStoryViewModel.postStory(imageMultipart, description, lat, lon)
            } else {
                postStoryViewModel.postStory(imageMultipart, description)
            }

        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_image_selected),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToHome() {
        val destinationPost =
            view?.findNavController()?.currentDestination == this.findNavController()
                .findDestination(R.id.navigation_postStory)
        val destinationHome =
            view?.findNavController()?.currentDestination == this.findNavController()
                .findDestination(R.id.navigation_home)

        if (destinationPost && !destinationHome) {
            val toDetailHomeFragment =
                PostStoryFragmentDirections.actionPostStoryFragmentToNavigationHome()
            toDetailHomeFragment.reFetch = true
            view?.findNavController()?.navigate(toDetailHomeFragment)
        }
    }

    private fun setUploadEnable() {
        val result = captionInput.text
        uploadBtn.isEnabled = (result != null && result.toString().isNotEmpty() && getFile != null)
    }

    private fun showLoading(b: Boolean) {
        binding.incProgress.progressOverlay.visibility = if (b) View.VISIBLE else View.GONE
    }

    private fun showError(error: String) {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog).apply {
            setTitle(getString(R.string.failed))
            setMessage(error)
            setNegativeButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun gpsDisabledDialog(context: Context) {
        AlertDialog.Builder(context, R.style.AlertDialog)
            .setTitle(getString(R.string.gps_disabled))
            .setMessage(getString(R.string.please_enable_gps))
            .setCancelable(true)
            .setPositiveButton("OK") { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )

        private val CAMERA_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )

        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}