package com.example.apfinalproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apfinalproject.MainActivity
import com.example.apfinalproject.databinding.MapFragmentBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.R
import com.example.apfinalproject.event.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.BoundingBox
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import java.util.Locale

class MapFragment : Fragment() {
    private var _binding: MapFragmentBinding? = null
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var navController : NavController
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0

    private fun initAdapter(binding: MapFragmentBinding, boundingBox: BoundingBox) {
        val rv = binding.eventRV
        rv.layoutManager = LinearLayoutManager(context)
        val adapter = PastEventAdapter(viewModel) {
            // Navigate to OneEvent
            navController.navigate(
                MapFragmentDirections.actionMapFragmentToOneEventFragment(it)
            )
        }
        rv.adapter = adapter
        // Get the min and max for the x and y coordinates
        val minX = boundingBox.lonWest
        val maxX = boundingBox.lonEast
        val minY = boundingBox.latSouth
        val maxY = boundingBox.latNorth
        // Log the values
        Log.d("MapFragment", "Min X: $minX, Max X: $maxX, Min Y: $minY, Max Y: $maxY")
        viewModel.setEvents(false)
        viewModel.observeNetTypeEvents().observe(viewLifecycleOwner) { events ->
            viewModel.observeLocations().observe(viewLifecycleOwner) { locations ->
                val filteredEvents = events.filterIndexed { index, _ ->
                    val lat = locations[index].latitude
                    val long = locations[index].longitude
                    lat in minY..maxY && long in minX..maxX
                }
                adapter.submitList(filteredEvents)
                if(filteredEvents.isEmpty()){
                    binding.noEvents.visibility = View.VISIBLE
                }
                else{
                    binding.noEvents.visibility = View.GONE
                }

                // Add a marker for each filtered event
                filteredEvents.forEachIndexed { index, event ->
                    val eventLocation = GeoPoint(locations[index].latitude.toDouble(), locations[index].longitude.toDouble())
                    val marker = Marker(mapView)
                    marker.position = eventLocation
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = event.title
                    mapView.overlays.add(marker)
                    Log.d("MapFragment", "Added marker at: ${marker.position.latitude}, ${marker.position.longitude}")
                }

                // Redraw the map
                mapView.invalidate()
            }
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.Builder(10000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setIntervalMillis(10000L)  // Desired interval for active location updates, in milliseconds.
            .setMinUpdateIntervalMillis(5000L)  // Fastest rate for active location updates, in milliseconds.
            .setMaxUpdates(1)  // Limits the total number of location updates.
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult ?: return
                val location = locationResult.lastLocation
                if (location != null) {
                    Log.d("MapFragment", "Updated Location: Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                    // Use the location as needed
                } else {
                    Log.d("MapFragment", "No location retrieved on update!")
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }
    
    private fun moveCamera(location: String) {
        val mapController = mapView.controller
        viewModel.setLocationTerm(location)
        viewModel.observeLocations().observe(viewLifecycleOwner) { locations ->
            Log.d("OSM", "MapFragment, observeLocations: $locations")
            if (locations.isNotEmpty()) {
                val lat = locations[0].latitude.toDouble()
                Log.d("OSM", "Lat: $lat")
                val long = locations[0].longitude.toDouble()
                Log.d("OSM", "Long: $long")
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
        val activityContext = requireActivity()

        if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the necessary permissions
            ActivityCompat.requestPermissions(activityContext, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activityContext)

        // Now it's safe to request new location data
        requestNewLocationData()

        mapView = binding.mapFrag
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d("MapFragment", "Last Location: $location")
                // Use the location object as needed
                if (location != null) {
                    userLatitude = location.latitude
                    userLongitude = location.longitude
                    Log.d("MapFragment", "Last Location: Latitude: $userLatitude, Longitude: $userLongitude")
                    Log.d("OSM", "Last Location: $location")
                    // Initialize the map controller here
                    val mapController = mapView.controller
                    mapController.setZoom(13.0)
                    mapController.setCenter(GeoPoint(userLatitude,userLongitude))
                    mapView.overlays.clear()

                    val userLocation = GeoPoint(location.latitude, location.longitude)

                    // Create a new Marker at the user's current location
                    val marker = Marker(mapView)
                    marker.position = userLocation
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = "Current Location"
                    marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.home, null)
                    // Add the marker to the map's overlays
                    mapView.overlays.add(marker)

                    // Get the current bounding box of the visible area
                    val boundingBox = mapView.boundingBox

                    // Set up adapter for event list
                    initAdapter(binding, boundingBox)
                }
                else {
                    Log.d("MapFragment", "Last location is null")
                    binding.noEvents.visibility = View.VISIBLE
                }
            }

        navController = findNavController()


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

    }

    override fun onDestroyView() {
        _binding = null
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        viewModel.showActionBar()
        super.onDestroyView()
    }
}