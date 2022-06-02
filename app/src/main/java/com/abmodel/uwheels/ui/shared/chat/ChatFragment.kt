package com.abmodel.uwheels.ui.shared.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.databinding.FragmentChatBinding
import com.abmodel.uwheels.ui.adapter.MessageItemAdapter
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.util.formatDateFromMillis
import com.abmodel.uwheels.util.formatTime
import com.abmodel.uwheels.util.getCurrentDateAsCustomDate

class ChatFragment : Fragment() {

	// Binding objects to access the view elements
	private var _binding: FragmentChatBinding? = null
	private val binding get() = _binding!!

	private val sharedViewModel: SharedViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentChatBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			dataViewModel = sharedViewModel
			lifecycleOwner = viewLifecycleOwner

			back.setOnClickListener {
				findNavController().navigateUp()
			}
			send.setOnClickListener {
				onSendPressed()
			}

			sharedViewModel.selectedChat.observe(viewLifecycleOwner) { chat ->
				chatName.text = chat.name
				chatDate.text = getString(
					R.string.chat_date,
					chat.date.millis?.let { formatDateFromMillis(it) },
					chat.date.hour?.let { chat.date.minute?.let { it1 -> formatTime(it, it1) } }
				)
			}

			// Set the recycler view adapter
			messages.adapter = MessageItemAdapter(
				userId = FirebaseAuthRepository.getInstance().getLoggedInUser().uid
			)

			sharedViewModel.startChatUpdates(
				sharedViewModel.selectedChatId.value!!
			)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		sharedViewModel.stopChatUpdates()
		_binding = null
	}

	private fun onSendPressed() {
		binding.input.text.toString().takeIf { it.isNotEmpty() }?.let { message ->
			sharedViewModel.sendMessage(
				message,
				getCurrentDateAsCustomDate().let {
					getString(
						R.string.date_format,
						formatDateFromMillis(it.millis!!),
						formatTime(it.hour!!, it.minute!!)
					)
				}
			)
			binding.input.text.clear()
		}
	}
}
