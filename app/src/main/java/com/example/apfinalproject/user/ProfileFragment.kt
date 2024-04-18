package com.example.apfinalproject.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import com.example.apfinalproject.event.Event

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
        viewModel.fetchMyEvents {myEvents ->
            Log.d(TAG, "fetchMyEvents size = ${myEvents.size}")
            if (myEvents.isEmpty()) {
                binding.pastEventsRV.visibility = View.GONE
                binding.noPastEvents.visibility = View.VISIBLE
            } else {
                binding.noPastEvents.visibility = View.GONE
                binding.pastEventsRV.visibility = View.VISIBLE

                val adapter = PastEventAdapter(viewModel) {
                    navController.navigate(ProfileFragmentDirections.actionProfileFragmentToOneEventFragment(it))
                }
                binding.pastEventsRV.adapter = adapter
                adapter.submitList(myEvents)
                binding.pastEventsRV.layoutManager = LinearLayoutManager(context)
            }
        }


        // Don't initialize the adapter if there are no events


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
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        viewModel.hideActionBar()

        initAdapters(binding)
        val user = args.User
        navController = findNavController()

        binding.userName.text = user.firstName
        binding.profileBio.text = user.bio
        viewModel.fetchUserImage(user.profileImage, binding.profileImage)

        if (user.id == viewModel.getActiveUser()?.id) {
            binding.editProfileButton.visibility = View.VISIBLE
            binding.editProfileButton.setOnClickListener {
                // Navigate to EditProfile
                navController.navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment())
            }
        } else {
            binding.editProfileButton.visibility = View.GONE
        }
        binding.backButton.setOnClickListener() {
            navController.popBackStack()
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}