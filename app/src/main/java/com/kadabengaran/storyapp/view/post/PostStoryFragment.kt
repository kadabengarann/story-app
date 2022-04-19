package com.kadabengaran.storyapp.view.post

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import com.kadabengaran.storyapp.R
import com.kadabengaran.storyapp.components.MyActionButton
import com.kadabengaran.storyapp.databinding.FragmentPostStoryBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.utils.createTempFile
import com.kadabengaran.storyapp.utils.reduceFileImage
import com.kadabengaran.storyapp.utils.uriToFile
import com.kadabengaran.storyapp.view.PreferenceViewModel
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
    private lateinit var preferenceViewModel: PreferenceViewModel

    private val factory by lazy {
        ViewModelFactory.getInstance()
    }
    private val postStoryViewModel: PostStoryViewModel by viewModels {
        factory
    }

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var captionInput: EditText
    private lateinit var uploadBtn: MyActionButton

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) Toast.makeText(
                requireContext(),
                getString(R.string.permission_failed),
                Toast.LENGTH_SHORT
            ).show()
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
        setupViewModel()
        setupView()
        setupAction()
        observeView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
        if (postStoryViewModel.getToken().value == null) {
            preferenceViewModel.getUser().observe(viewLifecycleOwner) {
                postStoryViewModel.postToken(it.token)
            }
        }
    }

    private fun setupView() {
        captionInput = binding.inpDescription
        uploadBtn = binding.btnUpload
        setUploadEnable()
    }

    private fun setupAction() {
        binding.btnCamera.setOnClickListener { startTakePhoto() }
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
            if (response != null) {
                when (response) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            "Story posted successfully!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        navigateToHome()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showError(response.error)
                        postStoryViewModel.resetProgress()
                    }
                }
            }
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
            setUploadEnable()

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
            postStoryViewModel.postStory(imageMultipart, description)
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
            view?.findNavController()?.navigate(R.id.action_postStoryFragment_to_navigation_home)
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

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}