package com.example.apfinalproject.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apfinalproject.ui.MainViewModel
import com.example.apfinalproject.ui.PastEventAdapter
import com.example.apfinalproject.databinding.ProfileFragmentBinding



class ProfileFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    private fun initAdapter(binding: ProfileFragmentBinding) {
        binding.pastEventsRV.layoutManager = LinearLayoutManager(context)
        val adapter = PastEventAdapter(viewModel) {
            // Navigate to OneEvent
        }
        binding.pastEventsRV.adapter = adapter
        val eventList = viewModel.observePastEvents()
        adapter.submitList(eventList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeActiveUser().observe(viewLifecycleOwner) {
            binding.userName.text = it.name
            binding.profileBio.text = it.bio
        }
        initAdapter(binding)
    }
}