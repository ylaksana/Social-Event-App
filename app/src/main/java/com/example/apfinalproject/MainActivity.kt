package com.example.apfinalproject

import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
import android.util.Log

//import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apfinalproject.api.ImageRepository
import com.example.apfinalproject.ui.EventAdapter

import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.apfinalproject.databinding.ActionBarBinding
import com.example.apfinalproject.databinding.ActivityMainBinding
import com.example.apfinalproject.MainActivity
import com.example.apfinalproject.ui.HomeFragment
import com.example.apfinalproject.ui.HomeFragmentDirections
import com.example.apfinalproject.ui.MainViewModel

class MainActivity : AppCompatActivity() {
    private var actionBarBinding: ActionBarBinding? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController : NavController

    private fun initActionBar(actionBar: ActionBar) {
        // Disable the default and enable the custom
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBarBinding = ActionBarBinding.inflate(layoutInflater)
        // Apply the custom view
        actionBar.customView = actionBarBinding?.root
        viewModel.initActionBarBinding(actionBarBinding!!)

    }

    // An Android nightmare
    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    // https://stackoverflow.com/questions/7789514/how-to-get-activitys-windowtoken-without-view
    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }


    // https://nezspencer.medium.com/navigation-components-a-fix-for-navigation-action-cannot-be-found-in-the-current-destination-95b63e16152e
    // You get a NavDirections object from a Directions object like
    // HomeFragmentDirections.
    // safeNavigate checks if you are in the source fragment for the directions
    // object and if not does nothing
    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.
        getAction(direction.actionId)?.
        run {
            navigate(direction)
        }
    }
    private fun actionBarTitleLaunchProfile() {
        // XXX Write me actionBarBinding, safeNavigate
        actionBarBinding?.profileButton?.setOnClickListener{
            navController.safeNavigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }
    }
    private fun actionBarLaunchMap() {
        // XXX Write me actionBarBinding, safeNavigate
        actionBarBinding?.mapButton?.setOnClickListener {
            navController.safeNavigate(HomeFragmentDirections.actionHomeFragmentToMapFragment())
        }
    }

    private fun actionBarLaunchSettings() {
        // XXX Write me
        actionBarBinding?.settingsButton?.setOnClickListener {
            navController.safeNavigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }
    }

    private fun actionBarCreateEvent() {
        // XXX Write me
        actionBarBinding?.createButton?.setOnClickListener {
            navController.safeNavigate(HomeFragmentDirections.actionHomeFragmentToCreateEventFragment())
        }
    }

    private fun actionBarEventList() {
        // XXX Write me
        actionBarBinding?.eventListButton?.setOnClickListener {
            navController.safeNavigate(HomeFragmentDirections.actionHomeFragmentToCreateEventFragment())
        }
    }

    private fun initTitleObservers() {
        // Observe title changes
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        Log.d("navController", "before1")
        setContentView(activityMainBinding.root)
        Log.d("navController", "before2")
        setSupportActionBar(activityMainBinding.toolbar)
        Log.d("navController", "before3")
        supportActionBar?.let{
            initActionBar(it)
        }
        Log.d("navController", "before4")

        initTitleObservers()
        actionBarTitleLaunchProfile()
        actionBarLaunchMap()
        actionBarLaunchSettings()
        actionBarCreateEvent()
        actionBarEventList()

        // Set up our nav graph
        Log.d("navController", "before5")
        navController = findNavController(R.id.main_frame)
        Log.d("navController", "passed")
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        // If we have a toolbar (not actionbar) we don't need to override
        // onSupportNavigateUp().
        activityMainBinding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController, appBarConfiguration)
    }
}