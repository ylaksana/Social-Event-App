package com.example.apfinalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.R
import com.example.apfinalproject.api.ImageRepository
import com.example.apfinalproject.databinding.ActivityMainBinding
import com.example.apfinalproject.databinding.FragmentRvBinding
import com.example.apfinalproject.event.EventAdapter

class EventListFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null
    private lateinit var navController : NavController
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
        navController = findNavController()

        // Set up Event RecyclerView
        val rv = binding.rv
        rv.layoutManager = LinearLayoutManager(context)

        val eventAdapter = EventAdapter(viewModel) {event ->
            navController.navigate(
                EventListFragmentDirections.actionEventListFragmentToOneEventFragment(event)
            )
        }
        rv.adapter = eventAdapter
        viewModel.observeEvents().observe(viewLifecycleOwner) {events ->
            Log.d("MainActivity", "eventList length: ${events.size}")
            eventAdapter.submitList(events)
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

        // Set the spinner item selection listener
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                when (selectedItem) {
                    "My Events" -> {
                        // Change the filter to "My Events"
                        viewModel.setEvents(true)
                    }

                    "My Requests" -> {
                        // Change the filter to "My Requests"
                        viewModel.setEvents(false)
                    }
                }
                viewModel.observeUserEvents().observe(viewLifecycleOwner) {
                    Log.d("filterSpinner", "filterList length: $it")
                    adapter?.submitList(it)
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.setEvents(true)
                viewModel.observeNetTypeEvents().observe(viewLifecycleOwner) {
                    Log.d("filterSpinner", "filterList length: $it")
                    adapter?.submitList(it)
                }
            }
        }

        rv.adapter = adapter
        navController = findNavController()
        binding.backButton.setOnClickListener{
            navController.popBackStack()

        }

    }

    override fun onDestroyView() {
        _binding = null
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        viewModel.showActionBar()
        super.onDestroyView()
    }
}