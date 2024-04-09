package com.example.apfinalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apfinalproject.R
import com.example.apfinalproject.api.ImageRepository
import com.example.apfinalproject.databinding.ActivityMainBinding
import com.example.apfinalproject.databinding.FragmentRvBinding
import com.example.apfinalproject.event.EventAdapter

class EventListFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null
    private lateinit var navController : NavController
    private val activityMainBinding: ActivityMainBinding? = null
    //     This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        viewModel.hideActionBar()

        // Move this to home fragment
        val rv = binding.rv
        rv.layoutManager = LinearLayoutManager(context)
        val imageRepo = context?.let {
            ImageRepository(it)
        }
        val adapter = imageRepo?.let { EventAdapter(viewModel, it) {event ->
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOneEventFragment(event))
        }
        }

        // Insert items into spinner
        val spinner = _binding?.titleTextView
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.list_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner?.adapter = adapter
        }


        viewModel.observeEvents().observe(viewLifecycleOwner) {
            Log.d("MainActivity", "eventList length: ${it.size}")
            adapter?.submitList(it)
        }

        rv.adapter = adapter
        navController = findNavController()
        binding.backButton.setOnClickListener{
            navController.popBackStack()
            viewModel.showActionBar()
        }

    }

    override fun onDestroyView() {
        _binding = null
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        super.onDestroyView()
    }
}