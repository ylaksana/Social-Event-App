package com.example.apfinalproject

import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
import android.util.Log

//import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels

import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.apfinalproject.databinding.ActionBarBinding
import com.example.apfinalproject.databinding.ActivityMainBinding
import com.example.apfinalproject.ui.HomeFragmentDirections
import com.example.apfinalproject.user.AuthUser
import com.example.apfinalproject.user.invalidUser

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }
    private var actionBarBinding: ActionBarBinding? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController : NavController
    private lateinit var authUser : AuthUser
    private var firstLogin: Boolean? = null

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
            navController.safeNavigate(HomeFragmentDirections.actionHomeFragmentToEventListFragment())
        }
    }

    private fun initTitleObservers() {
        // Observe title changes
    }

    fun getAuthUser(): AuthUser {
        return authUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        Log.d(TAG, ">>before authuser")
        authUser = AuthUser(activityResultRegistry)
        Log.d(TAG, ">>after authuser")
        lifecycle.addObserver(authUser)

        Log.d(TAG, ">>inflating binding")
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        Log.d(TAG, ">>setting content view")
        setContentView(activityMainBinding.root)
        Log.d(TAG, ">>setting support action bar")
        setSupportActionBar(activityMainBinding.toolbar)
        supportActionBar?.let{
            initActionBar(it)
        }


        initTitleObservers()
        actionBarTitleLaunchProfile()
        actionBarLaunchMap()
        actionBarLaunchSettings()
        actionBarCreateEvent()
        actionBarEventList()

        // Set up our nav graph
        navController = findNavController(R.id.main_frame)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        // If we have a toolbar (not actionbar) we don't need to override
        // onSupportNavigateUp().
        activityMainBinding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController, appBarConfiguration)
        Log.d(TAG, "onCreate end")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        var pendingLogin = false


        Log.d(TAG, ">>auth user: ${authUser.observeAuthId().value}")

        authUser.observeAuthId().observe(this) {authId ->
            if (!pendingLogin) {
                pendingLogin = true
                Log.d(TAG, ">>observeAuthId started with $authId")
                // XXX Write me, user status has changed
                if ((authId == null) or (authId == invalidUser.uid)) {
                    Log.d(TAG, ">> No user logged into Firebase : $authId")
                    viewModel.activeUser.postValue(invalidUser)
                } else {
                    Log.d(TAG, ">>User is logged in with uid $authId : ${authUser.getName()}")
                    viewModel.verifyUserAndLogin(authId) { user ->
                        if (user == invalidUser) {
                            navController.safeNavigate(
                                HomeFragmentDirections.actionHomeFragmentToCreateUserFragment(
                                    authId,
                                    authUser.getName(),
                                    authUser.getEmail()
                                )
                            )
                        }
                    }
                }
                pendingLogin = false
            }
        }
        Log.d(TAG, "onStart end")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }
}