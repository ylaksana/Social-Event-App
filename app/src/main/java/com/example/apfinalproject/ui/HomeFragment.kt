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
import com.example.apfinalproject.databinding.ContentMainBinding

class HomeFragment: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: ContentMainBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Set up the adapter and recycler view
    private fun initAdapter(binding: ContentMainBinding) {
//        val adapter = EventAdapter(viewModel) {}
//        val eventList = viewModel.observeEvents()
//        Log.d("HomeFragment", "eventList length: ${eventList.size}")
//        adapter.submitList(eventList)
//
//        binding.recyclerView.layoutManager = LinearLayoutManager(context)
//        binding.recyclerView.adapter = adapter

    }
//
//    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
//    }
//
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        // XXX Write me.  Set title based on current subreddit
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        initAdapter(binding)
    }
}