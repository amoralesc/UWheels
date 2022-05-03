package com.abmodel.uwheels.ui.passenger.search

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentRequestRideBinding
import com.abmodel.uwheels.util.formatDateFromMillis
import com.abmodel.uwheels.util.formatTime
import com.abmodel.uwheels.util.hideKeyboard
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class RequestRideFragment : Fragment(), OnMapReadyCallback {

	// Binding objects to access the view elements
	private var _binding: FragmentRequestRideBinding? = null
	private val binding get() = _binding!!

	private var mMap: GoogleMap? = null

	private lateinit var fusedLocationClient: FusedLocationProviderClient

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentRequestRideBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Setup the location provider client
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(
			requireActivity()
		)

		// Obtain the SupportMapFragment and get notified
		// when the map is ready to be used.
		val mapFragment = childFragmentManager
			.findFragmentById(R.id.map_search_ride) as? SupportMapFragment
		mapFragment?.getMapAsync(this)

		// Setup the date picker
		datePicker.addOnPositiveButtonClickListener { dateSelected ->
			// Set the time in the date EditText
			binding.date.setText(
				formatDateFromMillis(dateSelected)
			)
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
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
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

	/**
	 * Manipulates the map once available. The callback is triggered
	 * when the map is ready to be used.
	 */
	@SuppressLint("MissingPermission")
	override fun onMapReady(googleMap: GoogleMap) {
		mMap = googleMap

		// TODO: Last known location requires permissions
		fusedLocationClient.lastLocation
			.addOnSuccessListener { location: Location? ->
				// Got last known location. In some rare situations this can be null.
				if (location != null) {
					// Move the camera to the user's location
					mMap?.animateCamera(
						CameraUpdateFactory.newLatLngZoom(
							LatLng(
								location.latitude,
								location.longitude
							),
							15f
						)
					)
				}
			}
			.addOnFailureListener {
				Log.w(TAG, "Failed to get last known location")
				Log.w(TAG, it.message ?: "")
			}
			.addOnCanceledListener {
				Log.w(TAG, "Last known location task was cancelled")
			}
	}

	companion object {
		const val TAG = "RequestRideFragment"
	}
}
