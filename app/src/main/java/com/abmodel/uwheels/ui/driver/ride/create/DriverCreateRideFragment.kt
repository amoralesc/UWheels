package com.abmodel.uwheels.ui.driver.ride.create

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.CustomAddress
import com.abmodel.uwheels.data.model.Vehicle
import com.abmodel.uwheels.data.model.WheelsType
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.databinding.FragmentDriverCreateRideBinding
import com.abmodel.uwheels.ui.shared.search.SearchAddressFragment
import com.abmodel.uwheels.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class DriverCreateRideFragment : Fragment(), OnMapReadyCallback {
	companion object {
		const val TAG = "DriverCreateRideFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentDriverCreateRideBinding? = null
	private val binding get() = _binding!!

	private var mMap: GoogleMap? = null
	private var sourceMarker: Marker? = null
	private var destinationMarker: Marker? = null
	private var polyline: Polyline? = null

	private val viewModel: DriverCreateRideViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// Listen to results of the [SearchAddressFragment]
		setFragmentResultListener(
			SearchAddressFragment.REQUEST_KEY
		) { _, bundle ->

			val source: CustomAddress? = bundle.getParcelable(
				SearchAddressFragment.RESULT_KEY_SOURCE
			)
			val destination: CustomAddress? = bundle.getParcelable(
				SearchAddressFragment.RESULT_KEY_DESTINATION
			)

			viewModel.updateSourceAddress(source)
			viewModel.updateDestinationAddress(destination)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentDriverCreateRideBinding.inflate(inflater, container, false)
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
		datePicker.addOnPositiveButtonClickListener { selectedDate ->
			// Set the time in the date EditText
			binding.date.setText(
				formatDateFromMillis(selectedDate)
			)
			viewModel.updateDate(millis = selectedDate)
		}
		// Setup the time picker
		timePicker.addOnPositiveButtonClickListener {
			// Set the time in the time EditText
			binding.time.setText(
				formatTime(timePicker.hour, timePicker.minute)
			)
			viewModel.updateDate(
				hour = timePicker.hour,
				minute = timePicker.minute
			)
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

		binding.apply {
			source.setOnClickListener {
				goToSearchAddress("source")
			}
			destination.setOnClickListener {
				goToSearchAddress("destination")
			}

			create.setOnClickListener {
				onCreateRidePressed()
			}

			// Populate the wheels type spinner
			wheelsType.adapter = ArrayAdapter(
				requireContext(),
				android.R.layout.simple_spinner_dropdown_item,
				arrayOf(WheelsType.CLASSIC_WHEELS)
			)
			wheelsType.setSelection(0)

			val vehicles =
				FirebaseAuthRepository
					.getInstance()
					.getLoggedInUser()
					.vehicles

			Log.d(TAG, "Vehicles: $vehicles")

			vehicle.adapter = ArrayAdapter(
				requireContext(),
				android.R.layout.simple_spinner_dropdown_item,
				vehicles
			)

			viewModel.route.observe(viewLifecycleOwner) {
				drawRoute(it)
			}
			viewModel.sourceAddress.observe(viewLifecycleOwner) {
				source.setText(it?.mainText)
				drawSourceMarker(it?.latLng?.toLatLng())
			}
			viewModel.destinationAddress.observe(viewLifecycleOwner) {
				destination.setText(it?.mainText)
				drawDestinationMarker(it?.latLng?.toLatLng())
			}

			viewModel.result.observe(viewLifecycleOwner) { result ->
				result ?: return@observe
				result.message?.let {
					showMessage(it)
				}
				if (result.success) {
					findNavController().navigateUp()
				}
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun onCreateRidePressed() {
		viewModel.createRide(
			binding.wheelsType.selectedItem.toString(),
			binding.vehicle.selectedItem as Vehicle
		)
	}

	private fun goToSearchAddress(selectedInput: String) {
		val action =
			DriverCreateRideFragmentDirections
				.actionDriverCreateRideFragmentToSearchAddressFragment(
					selectedInput = selectedInput,
					source = null,
					destination = null
				)

		findNavController()
			.navigate(action)
	}

	private fun drawRoute(points: List<LatLng>?) {
		when {
			points == null || points.isEmpty() -> {
				polyline?.remove()
				polyline = null
				return
			}
			else -> {
				polyline?.remove()
				polyline = mMap?.addPolyline(
					PolylineOptions()
						.addAll(points)
						.color(ContextCompat.getColor(requireContext(), R.color.color_primary))
						.width(POLYLINE_WIDTH)
				)
				moveCameraToRoute()
			}
		}
	}

	private fun drawSourceMarker(latLng: LatLng?) {
		when {
			latLng == null -> {
				sourceMarker?.remove()
				sourceMarker = null
			}
			sourceMarker == null -> {
				sourceMarker = mMap?.addMarker(
					MarkerOptions()
						.position(latLng)
						.title("Source")
				)
			}
			else ->
				sourceMarker!!.position = latLng
		}
	}

	private fun drawDestinationMarker(latLng: LatLng?) {
		when {
			latLng == null -> {
				destinationMarker?.remove()
				destinationMarker = null
			}
			destinationMarker == null -> {
				destinationMarker = mMap?.addMarker(
					MarkerOptions()
						.position(latLng)
						.title("Destination")
				)
			}
			else ->
				destinationMarker!!.position = latLng
		}
	}

	/**
	 * Manipulates the map once available. The callback is triggered
	 * when the map is ready to be used.
	 */
	override fun onMapReady(googleMap: GoogleMap) {
		mMap = googleMap
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

	private fun moveCameraToRoute() {
		val bounds = LatLngBounds.Builder()
			.include(sourceMarker!!.position)
			.include(destinationMarker!!.position)
			.build()

		mMap!!.moveCamera(
			CameraUpdateFactory.newLatLngBounds(
				bounds,
				120
			)
		)
	}

	private val datePicker =
		MaterialDatePicker.Builder.datePicker()
			.build()

	private val timePicker =
		MaterialTimePicker.Builder()
			.setTimeFormat(TimeFormat.CLOCK_12H)
			.setHour(7)
			.setMinute(0)
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
