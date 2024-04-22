package com.example.apfinalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apfinalproject.MainActivity
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.HomeFragmentBinding
import com.example.apfinalproject.event.EventAdapter
import com.example.apfinalproject.interest.FilterAdapter
import com.example.apfinalproject.interest.InterestCategories

class HomeFragment : Fragment() {
    companion object {
        private const val TAG = "HomeFragment"
    }

    private val TAG = "HomeFragment"
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: HomeFragmentBinding? = null
    private lateinit var navController: NavController
    private val filtersList: List<InterestCategories.Interest> = InterestCategories.getInterests()

//     This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var adapter: EventAdapter? = null

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination
            ?.getAction(direction.actionId)
            ?.run {
                navigate(direction)
            }
    }

    private fun initTouchHelper(): ItemTouchHelper {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int,
                ) {
                    val position = viewHolder.bindingAdapterPosition
                    val event = adapter?.currentList?.get(position)
                    Log.d(TAG, "Swipe delete $direction")
                    Log.d(TAG, "adapter: $adapter")
                    Log.d(TAG, "position: $position")
                    Log.d(TAG, "event: ${adapter?.currentList}")
                    Log.d(TAG, "event: ${event?.title}")

                    event?.let {
                        val eventId = it.id
                        viewModel.addEventSwipe(eventId, direction)
                        viewModel.removeEventFromView(event)

                        // find new way to update, itemRemovedAt duplicated bound objects
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
    }

    private fun initAdapters(binding: HomeFragmentBinding) {
        Log.d(TAG, "initAdapters")
        // Event RV
        binding.eventRV.layoutManager = LinearLayoutManager(context)
//        Log.d(TAG, ">>initAdapters: ${location?.latitude}, ${location?.longitude}")
        adapter =
            EventAdapter(viewModel) { event ->
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToOneEventFragment(event),
                )
            }
        binding.eventRV.adapter = adapter

        val activeUser = viewModel.getActiveUser()
        viewModel.observeNetTypeEvents().observe(viewLifecycleOwner) { events ->
            Log.d("Filter", "filterList length: ${events.size}")
            events.filter { event ->
                !event.yesSwipes.contains(activeUser?.id) &&
                    !event.noSwipes.contains(activeUser?.id)
            }
            adapter?.submitList(events)
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
        initTouchHelper().attachToRecyclerView(binding.eventRV)
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
