package com.abmodel.uwheels.ui.passenger.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.databinding.FragmentPassengerHomeBinding
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.ui.shared.sensor.ShakeDetector
import com.abmodel.uwheels.util.DEBUG_USE_SENSORS
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The landing page of the app for a passenger user.
 */
class PassengerHomeFragment : Fragment() {

	companion object {
		const val TAG = "PassengerHomeFragment"

		enum class FromPassengerHomeFragmentDestination {
			PROFILE,
			HOSTED_RIDES,
			RIDES,
			CONTACTS,
			CHATS,
			REQUEST_RIDE,
			CREATE_RIDE,
			SETTINGS
		}
	}

	// Binding objects to access the view elements
	private var _binding: FragmentPassengerHomeBinding? = null
	private val binding get() = _binding!!

	private val mSensorManager: SensorManager by lazy {
		requireActivity().getSystemService(
			Context.SENSOR_SERVICE
		) as SensorManager
	}
	private val mAccelerometer: Sensor by lazy {
		mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
	}
	private val mShakeDetector: ShakeDetector = createShakeDetector()

	private fun createShakeDetector(): ShakeDetector {
		val shakeDetector = ShakeDetector()
		shakeDetector.setOnShakeListener(object : ShakeDetector.OnShakeListener {
			override fun onShake(count: Int) {
				Toast.makeText(requireContext(), "I feel shaken!", Toast.LENGTH_SHORT).show()
			}
		})
		return shakeDetector
	}

	private val sharedViewModel: SharedViewModel by activityViewModels()

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

		if (FirebaseAuthRepository.getInstance().isDriverModeOn()) {
			goToDriverHome(false)
		}

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
				goToNextScreen(FromPassengerHomeFragmentDestination.PROFILE)
			}
			hostedRides.setOnClickListener {
				goToNextScreen(FromPassengerHomeFragmentDestination.HOSTED_RIDES)
			}
			rides.setOnClickListener {
				goToNextScreen(FromPassengerHomeFragmentDestination.RIDES)
			}
			contacts.setOnClickListener {
				goToNextScreen(FromPassengerHomeFragmentDestination.CONTACTS)
			}
			chats.setOnClickListener {
				goToNextScreen(FromPassengerHomeFragmentDestination.CHATS)
			}
			requestRide.setOnClickListener {
				goToNextScreen(FromPassengerHomeFragmentDestination.REQUEST_RIDE)
			}
			createRide.setOnClickListener {
				goToNextScreen(FromPassengerHomeFragmentDestination.CREATE_RIDE)
			}
			settings.setOnClickListener {
				goToNextScreen(FromPassengerHomeFragmentDestination.SETTINGS)
			}
		}
	}

	override fun onResume() {
		super.onResume()
		if (DEBUG_USE_SENSORS) {
			mSensorManager.registerListener(
				mShakeDetector,
				mAccelerometer,
				SensorManager.SENSOR_DELAY_UI
			)
		}
	}

	override fun onPause() {
		if (DEBUG_USE_SENSORS) {
			mSensorManager.unregisterListener(mShakeDetector)
		}
		super.onPause()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	/**
	 * Navigate to the destination fragment
	 */
	private fun goToNextScreen(destination: FromPassengerHomeFragmentDestination) {
		when (destination) {
			FromPassengerHomeFragmentDestination.PROFILE -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_profileDetailsFragment
				)
			}
			FromPassengerHomeFragmentDestination.HOSTED_RIDES -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_hostedRidesFragment
				)
			}
			FromPassengerHomeFragmentDestination.RIDES -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_ridesFragment
				)
			}
			FromPassengerHomeFragmentDestination.CONTACTS -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_contactsFragment
				)
			}
			FromPassengerHomeFragmentDestination.CHATS -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_chatsFragment
				)
			}
			FromPassengerHomeFragmentDestination.REQUEST_RIDE -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_requestRideFragment
				)
			}
			FromPassengerHomeFragmentDestination.CREATE_RIDE -> {
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_passengerCreateRideFragment
				)
			}
			FromPassengerHomeFragmentDestination.SETTINGS -> {
				Log.i(TAG, "Not implemented yet")
			}
		}
	}

	/**
	 * Setup an alert dialog to ask the user if they want to go to the driver screen
	 * * If the user is a driver, it should navigate to the driver's home
	 * * If the user is not a driver, it should navigate to the become driver screen
	 */
	private fun onDriverModePressed() {
		AlertDialog
			.Builder(requireContext())
			.setTitle(getString(R.string.activate_driver_mode))
			.setPositiveButton(R.string.ok) { _, _ ->
				if (FirebaseAuthRepository.getInstance().isDriver()) {
					goToDriverHome()
				} else {
					binding.driverMode.isChecked = false
					findNavController().navigate(
						R.id.action_passengerHomeFragment_to_becomeDriverFragment
					)
				}
			}
			.setNegativeButton(R.string.cancel) { _, _ ->
				binding.driverMode.isChecked = false
			}
			.show()
	}

	private fun goToDriverHome(setDriverModeOn: Boolean = true) {
		if (setDriverModeOn) {
			sharedViewModel.driverModeChanged(true)
			lifecycleScope.launch(Dispatchers.Main) {
				FirebaseAuthRepository.getInstance().setDriverMode(true)
			}
		}
		findNavController().navigate(
			R.id.action_passengerHomeFragment_to_driverHomeFragment
		)
	}
}
