package com.example.apfinalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.apfinalproject.R
import com.example.apfinalproject.databinding.ActivityMainBinding
import com.example.apfinalproject.databinding.CreateEventBinding

class CreateEventFragment: Fragment(){
    //     XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: CreateEventBinding? = null
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

    // Function for populating spinners with their respective arrays
    private fun populateSpinner(spinner: Spinner?, array: Int) {
        ArrayAdapter.createFromResource(
            requireContext(),
            array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner?.adapter = adapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        viewModel.hideActionBar()

        val spinnerMinutes = binding.spinnerMinutes
        val spinnerAMPM = binding.spinnerAMPM
        val spinnerHours = binding.spinnerHours
        val spinnerEvent = binding.spinnerEvent
        val spinnerMonth = binding.spinnerMonth
        val spinnerDay = binding.spinnerDay
        val spinnerYear = binding.spinnerYear


        binding.backButton.setOnClickListener {
            navController = findNavController()
            navController.popBackStack()
            viewModel.showActionBar()
        }

        populateSpinner(spinnerMinutes, R.array.minutes_array)
        populateSpinner(spinnerAMPM, R.array.ampm_array)
        populateSpinner(spinnerHours, R.array.hours_array)
        populateSpinner(spinnerEvent, R.array.genre_array)
        populateSpinner(spinnerMonth, R.array.month_array)
        populateSpinner(spinnerDay, R.array.day_array)
        populateSpinner(spinnerYear, R.array.year_array)
    }
    override fun onDestroyView() {
        _binding = null
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        super.onDestroyView()
    }

}