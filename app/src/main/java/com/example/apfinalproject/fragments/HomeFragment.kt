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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apfinalproject.MainActivity
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.adapters.EventAdapter
import com.example.apfinalproject.adapters.FilterAdapter
import com.example.apfinalproject.databinding.HomeFragmentBinding
import com.example.apfinalproject.model.InterestCategories

class HomeFragment : Fragment() {
    companion object {
        const val TAG = "HomeFragment"
    }

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: HomeFragmentBinding? = null
    private lateinit var navController: NavController
    private val filtersList: List<InterestCategories.Interest> = InterestCategories.getInterests()

//     This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var eventAdapter: EventAdapter? = null


    private fun initAdapters(binding: HomeFragmentBinding) {
        Log.d(TAG, "initAdapters")
        // Event RV
        binding.eventRV.layoutManager = LinearLayoutManager(context)
        eventAdapter =
            EventAdapter(viewModel) { event ->
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToOneEventFragment(event),
                )
            }
        binding.eventRV.adapter = eventAdapter

        val activeUser = viewModel.getActiveUser()
        viewModel.observeNetTypeEvents().observe(viewLifecycleOwner) { events ->
            Log.d("Filter", "filterList length before: ${events.size}")
            events.filter { event ->
                !event.yesSwipes.contains(activeUser?.id) &&
                    !event.noSwipes.contains(activeUser?.id)
            }
            Log.d("Filter", "filterList length after: ${events.size}")
            eventAdapter?.submitList(events)
        }
        viewModel.observeUserLocation().observe(viewLifecycleOwner) {
            Log.d(TAG, "userLocation: ${it?.latitude}, ${it?.longitude}")
            eventAdapter?.updateUserLocation(it)
        }

        val filterAdapter = FilterAdapter(viewModel)
        val filterRV = binding.filtersRV
        filterRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        filterAdapter.submitList(filtersList)
        filterRV.adapter = filterAdapter
        Log.d(TAG, "end initadapters")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        viewModel.showActionBar()
        Log.d(TAG, "onViewCreated")
        navController = findNavController()

        initAdapters(binding)
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
