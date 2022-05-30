package com.abmodel.uwheels.ui.passenger.ride.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentPassengerCreateRideBinding
import com.abmodel.uwheels.ui.passenger.ride.request.RequestRideFragmentDirections
import com.abmodel.uwheels.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class CreateRideFragment : Fragment(), OnMapReadyCallback {

	companion object {
		const val TAG = "CreateRideFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentPassengerCreateRideBinding? = null
	private val binding get() = _binding!!

	private var mMap: GoogleMap? = null

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentPassengerCreateRideBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Obtain the SupportMapFragment and get notified
		// when the map is ready to be used.
		val mapFragment = childFragmentManager
			.findFragmentById(R.id.map_address_results) as? SupportMapFragment
		mapFragment?.getMapAsync(this)

		// Setup the date picker
		datePicker.addOnPositiveButtonClickListener { dateSelected ->
			// Set the time in the date EditText
			binding.date.setText(
				formatDateFromMillis(dateSelected)
			)

			// TODO: Also set the date in the view model
		}

		// Setup the time picker
		timePicker.addOnPositiveButtonClickListener {
			// Set the time in the time EditText
			binding.time.setText(
				formatTime(timePicker.hour, timePicker.minute)
			)

			// TODO: Also set the time in the view model
		}

		binding.date.apply {
			showSoftInputOnFocus = false
			setOnClickListener {
				hideKeyboard()
				showDatePicker()
			}
		}

		binding.time.apply {
			showSoftInputOnFocus = false
			setOnClickListener {
				hideKeyboard()
				showTimePicker()
			}
		}

		binding.source.apply {
			setOnClickListener {
				goToSearchAddress("source")
			}
		}

		binding.destination.apply {
			setOnClickListener {
				goToSearchAddress("destination")
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun goToSearchAddress(selectedInput: String) {
		val action =
			CreateRideFragmentDirections
				.actionCreateRideFragmentToSearchAddressFragment(
					selectedInput = selectedInput,
					source = null,
					destination = null
				)

		findNavController()
			.navigate(action)
	}

	/**
	 * Manipulates the map once available. The callback is triggered
	 * when the map is ready to be used.
	 */
	override fun onMapReady(googleMap: GoogleMap) {
		mMap = googleMap

		// Deactivate all ui controls
		mMap!!.uiSettings.setAllGesturesEnabled(false)
		// Enable compass back
		mMap!!.uiSettings.isCompassEnabled = true

		// Move camera to default city location
		moveCameraToDefault()
	}

	// TODO: This shouldn't be a constant, but a value that comes from the user
	private fun moveCameraToDefault() {
		mMap!!.moveCamera(
			CameraUpdateFactory.newLatLngZoom(
				LatLng(BOGOTA_LAT, BOGOTA_LNG),
				BOGOTA_ZOOM
			)
		)
	}

	private val datePicker =
		MaterialDatePicker.Builder.datePicker()
			.setTitleText("Select date")
			.build()

	private val timePicker =
		MaterialTimePicker.Builder()
			.setTimeFormat(TimeFormat.CLOCK_12H)
			.setHour(7)
			.setMinute(0)
			.setTitleText("Select time")
			.build()

	/**
	 * Shows a date picker dialog.
	 */
	private fun showDatePicker() {
		val fragment: Fragment? = childFragmentManager.findFragmentByTag("datePicker")
		if (fragment != null)
			childFragmentManager.beginTransaction().remove(fragment).commit()

		datePicker.show(childFragmentManager, "datePicker")
	}

	/**
	 * Shows a time picker dialog.
	 */
	private fun showTimePicker() {
		val fragment: Fragment? = childFragmentManager.findFragmentByTag("timePicker")
		if (fragment != null)
			childFragmentManager.beginTransaction().remove(fragment).commit()

		timePicker.show(childFragmentManager, "timePicker")
	}
}
