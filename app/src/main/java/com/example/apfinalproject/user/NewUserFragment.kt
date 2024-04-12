package com.example.apfinalproject.user

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.*
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.NewUserFragmentBinding
import com.example.apfinalproject.ui.InterestAdapter
import com.example.apfinalproject.model.InterestCategories
import java.util.UUID


class NewUserFragment : Fragment() {
    companion object {
        private const val TAG = "NewUserFragment"
    }
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: NewUserFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val args: NewUserFragmentArgs by navArgs()
    private var interestAdapter: InterestAdapter? = null
    private var newImageUri: MutableLiveData<Uri> = MutableLiveData()
    private var newImageUUID: String? = null

    private fun initAdapters(binding: NewUserFragmentBinding) {
        Log.d(TAG, "initAdapters")

        binding.interestsRV.layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }
        interestAdapter = InterestAdapter(viewModel, true)
        binding.interestsRV.adapter = interestAdapter
        interestAdapter!!.submitList(InterestCategories.getCategories())
        binding.interestsRV.layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }
    }

    private fun initListeners(binding: NewUserFragmentBinding) {
        Log.d(TAG, "initListeners")
        binding.profileImage.setOnClickListener {
            pickAndSetImage()
        }
        binding.saveButton.setOnClickListener {
            newImageUri.observe(viewLifecycleOwner, Observer {uri ->
                Log.d(TAG, "newImageUri.observe $uri")
                if (uri != null) {
                    uploadUserPhoto(uri) {
                        Log.d(TAG, "uploadUserPhoto onComplete")
                        // onComplete())
                        val newUser = User(uid = args.AuthUserId)
                        newUser.firstName = binding.firstNameET.text.toString()
                        newUser.lastName = binding.lastNameET.text.toString()
                        newUser.bio = binding.bioET.text.toString()
                        newUser.userInterests = interestAdapter!!.tempInterests
                        newUser.profileImage = newImageUUID ?: ""

                        viewModel.addNewUser(newUser)
                        navController.popBackStack()
                    }
                }
            })
        }
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
        _binding = NewUserFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        navController = findNavController()
        initAdapters(binding)
        initListeners(binding)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    private fun pickAndSetImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickLauncher.launch(intent)
    }

    private var imagePickLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d(TAG, "imagePickLauncher started")
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            newImageUri.value = data?.data
            Log.d(TAG, "imagePickLauncher uri: $newImageUri.value")
            binding.profileImage.setImageURI(newImageUri.value)
        }
        Log.d(TAG, "imagePickLauncher complete")
    }

    private fun uploadUserPhoto(imageUri: Uri, onComplete: () -> Unit) {
        // Upload the image to the server and set it as the user's profile image
        Log.d(TAG, "uploadUserPhoto started")
        val storage = viewModel.getStorage()
        newImageUUID = UUID.randomUUID().toString()
        Log.d(TAG, "uploadUserPhoto newImageUUID $newImageUUID")
        storage.uploadUserPhoto(imageUri, newImageUUID!!, "") {
            onComplete()
        }
    }
}