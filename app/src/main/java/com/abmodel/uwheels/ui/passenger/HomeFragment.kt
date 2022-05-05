package com.abmodel.uwheels.ui.passenger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentPassengerHomeBinding
import com.abmodel.uwheels.ui.shared.sensor.ShakeDetector
import com.google.firebase.auth.FirebaseAuth

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
				/*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
				Toast.makeText(requireContext(), "I feel shaken!", Toast.LENGTH_SHORT).show()
			}
		})
		return shakeDetector
	}

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

		createShakeDetector()

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

	override fun onResume() {
		super.onResume()
		// Add the following line to register the Session Manager Listener onResume
		mSensorManager.registerListener(
			mShakeDetector,
			mAccelerometer,
			SensorManager.SENSOR_DELAY_UI
		)
	}

	override fun onPause() { // Add the following line to unregister the Sensor Manager onPause
		mSensorManager.unregisterListener(mShakeDetector)
		super.onPause()
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
				// TODO: replace with actual profile fragment
				Log.i(TAG, "Not implemented yet")
				FirebaseAuth.getInstance().signOut()
				findNavController().navigate(
					R.id.action_passengerHomeFragment_to_loginFragment
				)
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
