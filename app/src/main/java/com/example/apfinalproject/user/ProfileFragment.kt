package com.example.apfinalproject.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.*
import com.example.apfinalproject.ui.InterestAdapter
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.ui.PastEventAdapter
import com.example.apfinalproject.databinding.ProfileFragmentBinding


class ProfileFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: ProfileFragmentBinding? = null
    private lateinit var navController : NavController
    private val binding get() = _binding!!
    private val TAG = "ProfileFragment"
    private val args: ProfileFragmentArgs by navArgs()

    private fun initAdapters(binding: ProfileFragmentBinding) {
        Log.d(TAG, "initAdapters")
        // Event RV
        binding.pastEventsRV.layoutManager = LinearLayoutManager(context)
        val adapter = PastEventAdapter(viewModel) {
            // Navigate to OneEvent
        }
        binding.pastEventsRV.adapter = adapter
        Log.d(TAG, "fetching my events")
        viewModel.fetchMyEvents {
            Log.d(TAG, "submitting list of size ${it.size}")
            adapter.submitList(it)
        }
        // TODO: hide pastEvents or replace with text view if empty

        // Interest RV
        val interestAdapter = InterestAdapter(viewModel)
        binding.interestsRV.adapter = interestAdapter
        viewModel.observeInterests().observe(viewLifecycleOwner) {
            interestAdapter.submitList(it)
        }
        binding.interestsRV.layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
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
        Log.d(TAG, "onCreateView")
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        initAdapters(binding)
        val user = args.User
        navController = findNavController()

        binding.userName.text = user.firstName
        binding.profileBio.text = user.bio
        viewModel.fetchUserImage(user.profileImage, binding.profileImage)

        if (user == viewModel.getActiveUser()) {
            binding.editProfileButton.visibility = View.VISIBLE
            binding.editProfileButton.setOnClickListener {
                // Navigate to EditProfile
                navController.navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment())
            }
        } else {
            binding.editProfileButton.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}