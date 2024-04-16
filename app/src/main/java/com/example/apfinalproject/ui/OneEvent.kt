package com.example.apfinalproject.ui

import android.graphics.drawable.Drawable
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
import androidx.navigation.fragment.navArgs
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.OneEventBinding
import com.example.apfinalproject.UserAdapter
import androidx.recyclerview.widget.LinearLayoutManager

import java.io.IOException
import com.example.apfinalproject.glide.Glide

class OneEvent: Fragment(){
    private val viewModel : MainViewModel by activityViewModels()
    private var _binding: OneEventBinding? = null
    private lateinit var navController : NavController
    //     This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val args: OneEventArgs by navArgs()

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
        _binding = OneEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        navController = findNavController()

        binding.profileLink.setOnClickListener{
            navController.safeNavigate(OneEventDirections.actionOneEventFragmentToProfileFragment())
        }

        binding.editEventButton.setOnClickListener {
            navController.safeNavigate(OneEventDirections.actionOneEventFragmentToCreateEventFragment())
        }

        binding.eventDescription.text = args.Event.description
        binding.eventLocation.text = args.Event.location
        binding.eventTime.text = args.Event.time
        binding.eventDate.text = args.Event.date
        binding.eventName.text = args.Event.title

        viewModel.fetchUserByUid(args.Event.creator) {
            it?.let {
                binding.posterName.text = it.firstName
                viewModel.fetchUserImage(it.profileImage, binding.profileImage)
            }
        }
        viewModel.fetchEventImage(args.Event.imageName, binding.eventImage)


        if (args.Event.yesSwipes.isNotEmpty() && viewModel.getActiveUser()?.uid == args.Event.creator) {
            binding.interestedUsersRV.visibility = View.VISIBLE
            val adapter = UserAdapter(viewModel) {clickedUser ->
                // On Click Callback
                viewModel.enterChatRoom(args.Event.creator, clickedUser.uid) {conv ->
                    navController.safeNavigate(OneEventDirections.actionOneEventFragmentToChatFragment(conv))
                }
            }
            binding.interestedUsersRV.adapter = adapter
            viewModel.fetchUsersByUids(args.Event.yesSwipes) {
                adapter.submitList(it)
            }
            binding.interestedUsersRV.layoutManager = LinearLayoutManager(context)
        } else {
            binding.interestedUsersRV.visibility = View.GONE
        }


//        // TODO: use glide.fetch
//        try {
//            // Open an input stream to read the image from the assets
//            Log.d("imageName","${args.Event.imageName}.jpg")
//            context?.assets?.open("${args.Event.imageName}.jpg").use { inputStream ->
//                // Convert the input stream into a Drawable
//                val drawable = Drawable.createFromStream(inputStream, null)
//                // Set the Drawable as the ImageView background
//                binding.eventImage.background = drawable
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            // Handle the exception, e.g., if the image file is not found
//        }

        binding.backButton.setOnClickListener{
            navController.popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        viewModel.showActionBar()
        super.onDestroyView()
    }

}
