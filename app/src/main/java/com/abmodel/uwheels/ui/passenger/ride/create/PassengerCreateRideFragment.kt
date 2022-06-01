package com.abmodel.uwheels.ui.passenger.ride.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.CustomAddress
import com.abmodel.uwheels.databinding.FragmentPassengerCreateRideBinding
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

class PassengerCreateRideFragment : Fragment(), OnMapReadyCallback {

	companion object {
		const val TAG = "PassengerCreateRideFragment"

		val WheelsTypeList = listOf(
			Triple(
				"Shared Wheels",
				arrayOf("Taxi", "Uber", "Beat", "Didi", "Cabify"),
				1..4
			),
			Triple(
				"We Wheels",
				arrayOf("Transmilenio", "SITP", "Bus"),
				1..10
			)
		)
	}

	// Binding objects to access the view elements
	private var _binding: FragmentPassengerCreateRideBinding? = null
	private val binding get() = _binding!!

	private var mMap: GoogleMap? = null
	private var sourceMarker: Marker? = null
	private var destinationMarker: Marker? = null
	private var polyline: Polyline? = null

	private val viewModel: PassengerCreateRideViewModel by viewModels()

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
				WheelsTypeList.map { it.first }
			)

			wheelsType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onNothingSelected(parent: AdapterView<*>?) {}

				override fun onItemSelected(
					parent: AdapterView<*>?,
					view: View?,
					position: Int,
					id: Long
				) {
					// Populate the transportation spinner
					transportation.adapter = ArrayAdapter(
						requireContext(),
						android.R.layout.simple_spinner_dropdown_item,
						WheelsTypeList.map { it.second }[position]
					)

					// Populate the capacity spinner
					capacity.adapter = ArrayAdapter(
						requireContext(),
						android.R.layout.simple_spinner_dropdown_item,
						WheelsTypeList.map { it.third.toList() }[position]
					)
				}
			}
			wheelsType.setSelection(0)
			transportation.setSelection(0)
			capacity.setSelection(0)

			viewModel.route.observe(viewLifecycleOwner) {
				drawRoute(it)
			}
			viewModel.sourceAddress.observe(viewLifecycleOwner) {
				source.setText(it?.mainText)
				drawSourceMarker(it?.latLng)
			}
			viewModel.destinationAddress.observe(viewLifecycleOwner) {
				destination.setText(it?.mainText)
				drawDestinationMarker(it?.latLng)
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
			binding.transportation.selectedItem.toString(),
			binding.capacity.selectedItem as Int,
		)
	}

	private fun goToSearchAddress(selectedInput: String) {
		val action =
			PassengerCreateRideFragmentDirections
				.actionPassengerCreateRideFragmentToSearchAddressFragment(
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
