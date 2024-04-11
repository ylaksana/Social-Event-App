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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.api.ImageRepository
import com.example.apfinalproject.databinding.ActivityMainBinding
import com.example.apfinalproject.databinding.HomeFragmentBinding
import com.example.apfinalproject.event.EventAdapter

class HomeFragment: Fragment() {
//     XXX initialize viewModel
    private val TAG = "HomeFragment"
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: HomeFragmentBinding? = null
    private lateinit var navController : NavController
    private val activityMainBinding: ActivityMainBinding? = null
//     This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.
        getAction(direction.actionId)?.
        run {
            navigate(direction)
        }
    }

    private fun initTouchHelper(): ItemTouchHelper {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            {
                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    val position = viewHolder.bindingAdapterPosition
                    Log.d(javaClass.simpleName, "Swipe delete $position")
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
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

        val rv = binding.eventRV

        rv.layoutManager = LinearLayoutManager(context)

        // TODO: use glide?
        val imageRepo = context?.let {
            ImageRepository(it)
        }

        Log.d(TAG, "imageRepo: $imageRepo")
        val adapter = imageRepo?.let { EventAdapter(viewModel, it) {event ->
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOneEventFragment(event))
            }
        }
        Log.d(TAG, "adapter: $adapter")
        rv.adapter = adapter
        viewModel.observeEvents().observe(viewLifecycleOwner) {
            Log.d(TAG, "eventList length: ${it.size}")
            adapter?.submitList(it)
        }


        initTouchHelper().attachToRecyclerView(rv)
        navController = findNavController()
        binding.chatButton.setOnClickListener{
            navController.safeNavigate(HomeFragmentDirections.actionHomeFragmentToChatListFragment())
        }
    }

}