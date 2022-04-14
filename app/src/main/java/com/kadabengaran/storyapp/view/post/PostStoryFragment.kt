package com.kadabengaran.storyapp.view.post

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import com.kadabengaran.storyapp.R
import com.kadabengaran.storyapp.ViewModelFactory
import com.kadabengaran.storyapp.databinding.FragmentPostStoryBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.remote.ApiConfig
import com.kadabengaran.storyapp.view.PreferenceViewModel
import com.kadabengaran.storyapp.utils.*
import com.kadabengaran.storyapp.utils.createTempFile
import com.kadabengaran.storyapp.view.home.HomeViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PostStoryFragment : Fragment() {

    private var _binding: FragmentPostStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceViewModel: PreferenceViewModel

    private val factory by lazy {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val postStoryViewModel: PostStoryViewModel by viewModels {
        factory
    }

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var token: String? = null

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(requireContext(),"Tidak mendapatkan permission.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val actionBar = (activity as AppCompatActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.title_post_story)
        _binding = FragmentPostStoryBinding.inflate(inflater, container, false)
        val view: View = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        /*binding.btnUpload.setOnClickListener {
            view.findNavController().navigate(R.id.action_postStoryFragment_to_navigation_home)
        }*/
        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener {
            uploadImage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
        preferenceViewModel.getUser().observe(viewLifecycleOwner) { user ->
            postStoryViewModel.setToken(user.token)
        }
        if (token == null) {
            preferenceViewModel.getUser().observe(viewLifecycleOwner) {
                token = it.token
            }
        }
    }
    private fun startCameraX() {
        val intent = Intent (activity, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            /*val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )*/
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)

            binding.previewImageView.setImageBitmap(result)
        }
    }
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
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
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.let { intent.resolveActivity(it.packageManager) }
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
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding.inpDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            token?.let { postStoryViewModel.postStory(it, imageMultipart, description) }

            postStoryViewModel.uploadResult.observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    when (response) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), "Story Posted!1!1!", Toast.LENGTH_SHORT)
                                .show()
                            view?.findNavController()?.navigate(R.id.action_postStoryFragment_to_navigation_home)
                            Log.d(TAG, "procc_postStories: ${response.data.message}")
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showError(response.error)
                        }
                    }
                }
            }

        } else {
            Toast.makeText(
                requireContext(),
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(b: Boolean) {
        Log.d(TAG, "procc_postStories: LOADING.....")
        binding.progressBar.visibility = if (b) View.VISIBLE else View.GONE

    }

    private fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT)
            .show()

        Log.d(TAG, "proce_showError: $error")
    }
}