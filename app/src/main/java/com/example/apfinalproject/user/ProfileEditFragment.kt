package com.example.apfinalproject.user

import android.content.Intent
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.ProfileEditFragmentBinding
import java.util.UUID

class ProfileEditFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: ProfileEditFragmentBinding? = null
    private lateinit var navController : NavController
    private var newImageUri: Uri? = null
    private var newImageUUID: String? = null
    private val binding get() = _binding!!
    private val TAG = "ProfileFragment"

    private fun initAdapters(binding: ProfileEditFragmentBinding) {
        Log.d(TAG, "initAdapters")

//        binding.interestsRV.layoutManager = FlexboxLayoutManager(context).apply {
//            flexDirection = FlexDirection.ROW
//            flexWrap = FlexWrap.WRAP
//            justifyContent = JustifyContent.FLEX_START
//        }
//        val interestAdapter = InterestAdapter(viewModel)
//        binding.interestsRV.adapter = interestAdapter
//        viewModel.observeInterests().observe(viewLifecycleOwner) {
//            interestAdapter.submitList(it)
//        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = ProfileEditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
//        initAdapters(binding)
        val user = viewModel.getActiveUser()
        binding.userName.text = user.firstName

        if (user.bio != "") {
            binding.profileBio.setText(user.bio)
        } else {
            binding.profileBio.hint = "Enter Bio Here..."
        }

        viewModel.fetchUserImage(user.profileImage, binding.profileImage)

        binding.saveButton.setOnClickListener {
            // Save the new bio to the user and exit
            val newBio = binding.profileBio.text.toString()

            val newUser = user.copy()
            newUser.bio = newBio

            if (newImageUri != null) {
                Log.d(TAG, "Image URI: $newImageUri")
                uploadUserPhoto(newImageUri!!) {
                    newUser.profileImage = newImageUUID!!
                    viewModel.updateUser(newUser)
                    navController.popBackStack()
                }
            } else {
                viewModel.updateUser(newUser)
                navController.popBackStack()
            }
        }

        binding.backButton.setOnClickListener {
            // Exit without saving changes
            navController.popBackStack()
        }

        binding.profileImage.setOnClickListener {
            pickAndSetImage()
        }
    }

    private fun pickAndSetImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickLauncher.launch(intent)
    }

    private var imagePickLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            newImageUri = data?.data!!
            binding.profileImage.setImageURI(newImageUri)
        }
    }

    private fun uploadUserPhoto(imageUri: Uri, onComplete: () -> Unit) {
        // Upload the image to the server and set it as the user's profile image
        val storage = viewModel.getStorage()
        val oldImageUUID = viewModel.getActiveUser().profileImage
        newImageUUID = UUID.randomUUID().toString()
        storage.uploadUserPhoto(imageUri, newImageUUID!!, oldImageUUID) {
            onComplete()
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}