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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.ViewModelDBHelper
import com.example.apfinalproject.chat.ChatDBHelper
import com.example.apfinalproject.chat.ChatListAdapter
import com.example.apfinalproject.databinding.ChatListFragmentBinding

class ChatListFragment : Fragment() {
    private val TAG = "ChatListFragment"
    private val viewModel: MainViewModel by activityViewModels()
    private val chatDBHelper = ChatDBHelper()
    private val DBHelper = ViewModelDBHelper()
    private val chatListAdapter: ChatListAdapter? = null
    private lateinit var binding: ChatListFragmentBinding

//    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination
            ?.getAction(direction.actionId)
            ?.run {
                navigate(direction)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d(TAG, "onCreateView")
        binding = ChatListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        navController = findNavController()

        val chatListAdapter =
            ChatListAdapter(viewModel, ViewModelDBHelper()) { conversation ->
                val action = ChatListFragmentDirections.actionChatListFragmentToChatFragment(conversation)
                navController.safeNavigate(action)
            }
        val activeUserId = viewModel.observeActiveUser().value?.id

        activeUserId?.let { id ->
            viewModel.fetchConversationsByUserID(id) { convs ->
                chatListAdapter.submitList(convs)
            }
        }

        binding.chatListRV.layoutManager = LinearLayoutManager(context)
        binding.chatListRV.adapter = chatListAdapter

        binding.backButton.setOnClickListener {
            navController.navigate(ChatListFragmentDirections.actionChatListFragmentToHomeFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        viewModel.showActionBar()
    }
}
