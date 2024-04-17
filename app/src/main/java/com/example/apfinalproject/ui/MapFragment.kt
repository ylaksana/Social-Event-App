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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apfinalproject.databinding.MapFragmentBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.example.apfinalproject.MainViewModel

class MapFragment : Fragment() {
    private var _binding: MapFragmentBinding? = null
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var navController : NavController
    private val binding get() = _binding!!
    private lateinit var mapView: MapView

    private fun initAdapter(binding: MapFragmentBinding){
        val rv = binding.eventRV
        rv.layoutManager = LinearLayoutManager(context)
        val adapter = PastEventAdapter(viewModel){
            // Navigate to OneEvent
            navController.navigate(
                MapFragmentDirections.actionMapFragmentToOneEventFragment(it)
            )
        }
        rv.adapter = adapter
        viewModel.setEvents(false)
        viewModel.observeNetTypeEvents().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
    
    private fun moveCamera(location: String) {
        val mapController = mapView.controller
        viewModel.setLocationTerm(location)
        viewModel.observeLocations().observe(viewLifecycleOwner) { locations ->
            if (locations.isNotEmpty()) {
                val lat = locations[0].latitude.toDouble()
                val long = locations[0].longitude.toDouble()
                mapController.setZoom(17.0)
                mapController.setCenter(GeoPoint(lat, long))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MapFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        // Set up adapter for event list
        initAdapter(binding)

        // Back Button
        _binding?.backButton?.setOnClickListener{
            navController.popBackStack()
        }

        // Go Button
        _binding?.goBut?.setOnClickListener{
            moveCamera(binding.searchBar.text.toString())
        }

        // Set user agent to prevent getting banned from the OSM servers
        Configuration.getInstance().userAgentValue = "SwipeMeet"

        mapView = binding.mapFrag
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)

        // Add a marker
        val marker = Marker(mapView)
        // Set initial location term
        viewModel.setLocationTerm("Austin")

        viewModel.observeLocations().observe(viewLifecycleOwner) { locations ->
            if (locations.isNotEmpty()) {
                val startLocation = locations[0]
                (Log.d("MapFragment", "Location: $startLocation"))
                val startLat = startLocation.latitude.toDouble()
                val startLong = startLocation.longitude.toDouble()

                // Initialize the map controller here
                val mapController = mapView.controller
                mapController.setZoom(15.0)
                mapController.setCenter(GeoPoint(startLat, startLong))
            }
        }


    }

    override fun onDestroyView() {
        _binding = null
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        viewModel.showActionBar()
        super.onDestroyView()
    }
}