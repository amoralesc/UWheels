package com.abmodel.uwheels.ui.passenger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentPassengerHomeBinding

enum class FromHomeFragmentDestination {
	PROFILE,
	RIDES,
	CONTACTS,
	CHATS,
	REQUEST_RIDE,
	CREATE_RIDE,
	SETTINGS
}

/**
 * The landing page of the app for a passenger user.
 */
class HomeFragment : Fragment() {

	companion object {
		const val TAG = "HomeFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentPassengerHomeBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentPassengerHomeBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Set the click listeners for the navigation buttons
		binding.apply {
			userImage.setOnClickListener {
				goToNextScreen(FromHomeFragmentDestination.PROFILE)
			}
			myRides.setOnClickListener {
				goToNextScreen(FromHomeFragmentDestination.RIDES)
			}
			myContacts.setOnClickListener {
				goToNextScreen(FromHomeFragmentDestination.CONTACTS)
			}
			chats.setOnClickListener {
				goToNextScreen(FromHomeFragmentDestination.CHATS)
			}
			requestRide.setOnClickListener {
				goToNextScreen(FromHomeFragmentDestination.REQUEST_RIDE)
			}
			createRide.setOnClickListener {
				goToNextScreen(FromHomeFragmentDestination.CREATE_RIDE)
			}
			settings.setOnClickListener {
				goToNextScreen(FromHomeFragmentDestination.SETTINGS)
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	/**
	 * Navigate to the destination fragment
	 */
	private fun goToNextScreen(destination: FromHomeFragmentDestination) {
		when (destination) {
			FromHomeFragmentDestination.PROFILE -> {
				Log.i(TAG, "Not implemented yet")
			}
			FromHomeFragmentDestination.RIDES -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_myRidesFragment
				)
			}
			FromHomeFragmentDestination.CONTACTS -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_contactsFragment
				)
			}
			FromHomeFragmentDestination.CHATS -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_chatsFragment
				)
			}
			FromHomeFragmentDestination.REQUEST_RIDE -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_requestRideFragment
				)
			}
			FromHomeFragmentDestination.CREATE_RIDE -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_passengerCreateRideFragment
				)
			}
			FromHomeFragmentDestination.SETTINGS -> {
				Log.i(TAG, "Not implemented yet")
			}
		}
	}
}
