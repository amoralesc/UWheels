package com.abmodel.uwheels.ui.shared.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.Chat
import com.abmodel.uwheels.databinding.FragmentChatsBinding
import com.abmodel.uwheels.ui.adapter.ChatItemAdapter
import com.abmodel.uwheels.ui.shared.data.SharedViewModel

class ChatsFragment : Fragment() {

	// Binding objects to access the view elements
	private var _binding: FragmentChatsBinding? = null
	private val binding get() = _binding!!

	private val sharedViewModel: SharedViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentChatsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			dataViewModel = sharedViewModel
			lifecycleOwner = viewLifecycleOwner

			// Set the adapter for the recycler view
			chats.adapter = ChatItemAdapter {
				onChatSelected(it)
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun onChatSelected(chat: Chat) {
		sharedViewModel.selectChat(chat.id)
		findNavController().navigate(
			R.id.action_chatsFragment_to_chatFragment
		)
	}
}
