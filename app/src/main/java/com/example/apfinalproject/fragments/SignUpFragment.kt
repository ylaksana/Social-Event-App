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
import com.example.apfinalproject.databinding.SignupFragmentBinding
import com.example.apfinalproject.databinding.WelcomeFragmentBinding
import com.google.android.material.snackbar.Snackbar
import org.osmdroid.tileprovider.modules.IFilesystemCache

class SignUpFragment: Fragment(){

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var navController: NavController
    private var _binding: SignupFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SignupFragmentBinding.inflate(inflater, container, false)
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
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        binding.createAccountButton.setOnClickListener {
            if(email.isBlank() || password.isBlank() || confirmPassword.isBlank()){
                Log.d("SignUpFragment", "One or more fields are empty")
                Snackbar.make(view, "One or more fields are empty. Please fill out all fields.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            else if(confirmPassword != password){
                Log.d("SignUpFragment", "Passwords do not match")
                Snackbar.make(view, "Passwords do not match. Please confirm your password again.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //TODO: Implement Firebase Authentication
            else{
                Log.d("SignUpFragment", "Creating account")
                navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToHomeFragment())
            }
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