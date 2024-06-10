package com.example.apfinalproject.fragments
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.apfinalproject.MainActivity
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.LoginFragmentBinding
import com.example.apfinalproject.databinding.WelcomeFragmentBinding
import com.example.apfinalproject.fragments.CreateEventFragment.Companion.TAG
import com.google.firebase.auth.FirebaseAuth

class LoginFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var navController: NavController
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
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

        binding.loginButton.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())

            //TODO: Implement Firebase Authentication
            if (email.isNotBlank() && password.isNotBlank()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = FirebaseAuth.getInstance().currentUser
                            navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please enter email and password.",
                    Toast.LENGTH_SHORT).show()
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