package com.example.apfinalproject.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.EditEventFragmentBinding
import com.example.apfinalproject.databinding.ProfileEditFragmentBinding
import com.example.apfinalproject.user.ProfileEditFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apfinalproject.ViewModelDBHelper
import com.example.apfinalproject.chat.ChatListAdapter
import com.example.apfinalproject.databinding.ChatListFragmentBinding


class EditEventFragment : Fragment() {
    companion object {
        private const val TAG = "EditEventFragment"
    }
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: EditEventFragmentBinding? = null
//    private var newImageUri: MutableLiveData<Uri> = MutableLiveData()
//    private var newImageUUID: String? = null
    private val binding get() = _binding!!
    private val args: EditEventFragmentArgs by navArgs()
    private lateinit var navController : NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = EditEventFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        bindEventInfo()
    }

    private fun bindEventInfo() {
        binding.eventNameET.setText(args.Event.title)
        binding.eventDescriptionET.setText(args.Event.description)
        binding.eventLocationET.setText(args.Event.location)
        binding.eventDateET.setText(args.Event.date)
        binding.eventTimeET.setText(args.Event.time)
        viewModel.fetchEventImage(args.Event.imageName, binding.eventImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}