package com.example.apfinalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apfinalproject.model.ImageRepository
import com.example.apfinalproject.databinding.ActivityMainBinding
import com.example.apfinalproject.databinding.HomeFragmentBinding
import com.example.apfinalproject.model.EventAdapter

class HomeFragment: Fragment() {
//     XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: HomeFragmentBinding? = null
    private lateinit var navController : NavController
    private val activityMainBinding: ActivityMainBinding? = null
//     This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Set up the adapter and recycler view
    private fun initAdapter(binding: HomeFragmentBinding) {
        binding.eventRV.layoutManager = LinearLayoutManager(context)
        val imageRepo = ImageRepository(context)

        val adapter = EventAdapter(viewModel, imageRepo) {
            // Navigate to OneEvent
//            val action = HomeFragmentDirections.actionHomeFragmentToOneEventFragment(it)
//            findNavController().navigate(action)
        }

        val eventList = viewModel.observeEvents()
        Log.d("MainActivity", "eventList length: ${eventList.size}")
        adapter.submitList(eventList)
        binding.eventRV.adapter = adapter
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {

    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.
        getAction(direction.actionId)?.
        run {
            navigate(direction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        // Move this to home fragment
        val rv = binding.eventRV
        rv.layoutManager = LinearLayoutManager(context)
        val imageRepo = context?.let {
            ImageRepository(it)
        }
        val adapter = imageRepo?.let { EventAdapter(viewModel, it) {event ->
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOneEventFragment(event))
            }
        }
        val eventList = viewModel.observeEvents()
        Log.d("MainActivity", "eventList length: ${eventList.size}")
        adapter?.submitList(eventList)
        rv.adapter = adapter
        navController = findNavController()
        binding.chatButton.setOnClickListener{
            navController.safeNavigate(HomeFragmentDirections.actionHomeFragmentToChatFragment())
        }
        initAdapter(binding)
    }
}
