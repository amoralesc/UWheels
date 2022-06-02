package com.abmodel.uwheels.ui.driver.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.databinding.FragmentDriverHomeBinding
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DriverHomeFragment : Fragment() {

	companion object {
		const val TAG = "DriverHomeFragment"

		enum class FromDriverHomeFragmentDestination {
			PROFILE,
			HOSTED_RIDES,
			RIDES,
			CONTACTS,
			CHATS,
			CREATE_RIDE,
			SETTINGS
		}
	}

	// Binding objects to access the view elements
	private var _binding: FragmentDriverHomeBinding? = null
	private val binding get() = _binding!!

	private val sharedViewModel: SharedViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentDriverHomeBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			FirebaseAuthRepository.getInstance().getLoggedInUser().let { user ->
				// Set the greeting
				userName.text = getString(
					R.string.greeting_name,
					user.name
				)

				// Set the profile photo
				if (user.photoUrl != null) {
					Glide.with(requireContext())
						.load(user.photoUrl)
						.placeholder(R.drawable.ic_account_circle)
						.error(R.drawable.ic_account_circle)
						.into(profilePhoto)
				}
			}

			// Set the click listeners for the navigation buttons
			// Driver mode needs an additional check
			driverMode.setOnClickListener {
				onDriverModePressed()
			}
			// The other buttons are set to navigate to the corresponding destinations
			profilePhoto.setOnClickListener {
				goToNextScreen(FromDriverHomeFragmentDestination.PROFILE)
			}
			hostedRides.setOnClickListener {
				goToNextScreen(FromDriverHomeFragmentDestination.HOSTED_RIDES)
			}
			rides.setOnClickListener {
				goToNextScreen(FromDriverHomeFragmentDestination.RIDES)
			}
			contacts.setOnClickListener {
				goToNextScreen(FromDriverHomeFragmentDestination.CONTACTS)
			}
			chats.setOnClickListener {
				goToNextScreen(FromDriverHomeFragmentDestination.CHATS)
			}
			createRide.setOnClickListener {
				goToNextScreen(FromDriverHomeFragmentDestination.CREATE_RIDE)
			}
			settings.setOnClickListener {
				goToNextScreen(FromDriverHomeFragmentDestination.SETTINGS)
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
	private fun goToNextScreen(destination: FromDriverHomeFragmentDestination) {
		when (destination) {
			FromDriverHomeFragmentDestination.PROFILE -> {
				findNavController().navigate(
					R.id.action_driverHomeFragment_to_profileDetailsFragment
				)
			}
			FromDriverHomeFragmentDestination.HOSTED_RIDES -> {
				findNavController().navigate(
					R.id.action_driverHomeFragment_to_hostedRidesFragment
				)
			}
			FromDriverHomeFragmentDestination.RIDES -> {
				findNavController().navigate(
					R.id.action_driverHomeFragment_to_ridesFragment
				)
			}
			FromDriverHomeFragmentDestination.CONTACTS -> {
				Log.i(TAG, "Not implemented yet")
			}
			FromDriverHomeFragmentDestination.CHATS -> {
				findNavController().navigate(
					R.id.action_driverHomeFragment_to_chatsFragment
				)
			}
			FromDriverHomeFragmentDestination.CREATE_RIDE -> {

			}
			FromDriverHomeFragmentDestination.SETTINGS -> {
				Log.i(TAG, "Not implemented yet")
			}
		}
	}


	/**
	 * Setup an alert dialog to ask the user if they want to go to the passenger screen
	 */
	private fun onDriverModePressed() {
		AlertDialog
			.Builder(requireContext())
			.setTitle(getString(R.string.deactivate_driver_mode))
			.setPositiveButton(R.string.ok) { _, _ ->
				goToPassengerHome()
			}
			.setNegativeButton(R.string.cancel) { _, _ ->
				binding.driverMode.isChecked = true
			}
			.show()
	}

	private fun goToPassengerHome(setDriverModeOff: Boolean = true) {
		if (setDriverModeOff) {
			sharedViewModel.driverModeChanged(false)
			lifecycleScope.launch(Dispatchers.Main) {
				FirebaseAuthRepository.getInstance().setDriverMode(false)
			}
		}
		findNavController().navigate(
			R.id.action_driverHomeFragment_to_passengerHomeFragment
		)
	}
}
