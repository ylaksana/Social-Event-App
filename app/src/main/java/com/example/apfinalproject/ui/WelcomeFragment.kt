package com.example.apfinalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.WelcomeFragmentBinding


class WelcomeFragment : Fragment() {
    companion object {
        private const val TAG = "WelcomeFragment"
    }
    private var _binding: WelcomeFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
//    private val args: WelcomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, ">>onCreateView")
        _binding = WelcomeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        navController = findNavController()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ">>onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToHomeFragment())
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, ">>onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}