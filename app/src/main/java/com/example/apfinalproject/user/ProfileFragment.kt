package com.example.apfinalproject.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.*
import com.example.apfinalproject.ui.InterestAdapter
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.ui.PastEventAdapter
import com.example.apfinalproject.databinding.ProfileFragmentBinding

class ProfileFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ProfileFragment"

    private fun initAdapters(binding: ProfileFragmentBinding) {
        Log.d(TAG, "initAdapters")
        binding.pastEventsRV.layoutManager = LinearLayoutManager(context)
        val adapter = PastEventAdapter(viewModel) {
            // Navigate to OneEvent
        }
        binding.pastEventsRV.adapter = adapter
        viewModel.observePastEvents().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        binding.interestsRV.layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }
        val interestAdapter = InterestAdapter(viewModel)
        binding.interestsRV.adapter = interestAdapter
        viewModel.observeInterests().observe(viewLifecycleOwner) {
            interestAdapter.submitList(it)
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
        val user = viewModel.getActiveUser()
        binding.userName.text = user.firstName
        binding.profileBio.text = user.bio
        viewModel.fetchUserImage(user.profileImage, binding.profileImage)
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}