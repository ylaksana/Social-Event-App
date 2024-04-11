package com.example.apfinalproject.ui

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.apfinalproject.databinding.SettingsFragmentBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apfinalproject.MainActivity


class SettingsFragment: Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutButton.setOnClickListener {
            (activity as MainActivity).getAuthUser().logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}