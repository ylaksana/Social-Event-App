package com.example.apfinalproject.fragments
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.apfinalproject.MainActivity
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.HomeFragmentBinding
import com.example.apfinalproject.databinding.WelcomeFragmentBinding

class WelcomeFragment: Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var navController: NavController
    private var _binding: WelcomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = WelcomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        viewModel.showActionBar()
        navController = findNavController()

        binding.loginButton.setOnClickListener {
            navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
        }

        binding.signUpButton.setOnClickListener {
            navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToSignUpFragment())
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).requestLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).stopLocationUpdates()
    }
}