package com.abmodel.uwheels.ui.passenger.ride.request

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.*
import com.abmodel.uwheels.databinding.FragmentRequestRideBinding
import com.abmodel.uwheels.ui.adapter.SearchedRideItemAdapter
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.ui.shared.data.SharedViewModelFactory
import com.abmodel.uwheels.ui.shared.search.SearchAddressFragment
import com.abmodel.uwheels.util.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class RequestRideFragment : Fragment(), OnMapReadyCallback {

	companion object {
		const val TAG = "RequestRideFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentRequestRideBinding? = null
	private val binding get() = _binding!!

	private val viewModel: RequestRideViewModel by viewModels()
	private val sharedViewModel: SharedViewModel by activityViewModels {
		SharedViewModelFactory(requireActivity().application)
	}

	private var mMap: GoogleMap? = null
	private var sourceMarker: Marker? = null
	private var destinationMarker: Marker? = null
	private var polyline: Polyline? = null

	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private val locationRequest = createLocationRequest()
	private val locationCallback = createLocationCallback()

	private val mSensorManager: SensorManager by lazy {
		requireActivity().getSystemService(
			Context.SENSOR_SERVICE
		) as SensorManager
	}
	private val lightSensor: Sensor by lazy {
		mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
	}
	private val lightEventListener: SensorEventListener = createLightEventListener()

	private var hasLocationPermissions = false
	private var locationSettingsEnabled = false

	private var millis: Long? = null
	private var hour: Int? = null
	private var minute: Int? = null

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
			onInputChange()
		}
	}

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

		// Obtain the SupportMapFragment and get notified
		// when the map is ready to be used.
		val mapFragment = childFragmentManager
			.findFragmentById(R.id.map_request_ride) as? SupportMapFragment
		mapFragment?.getMapAsync(this)

		// Setup the location provider client
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(
			requireActivity()
		)
		checkLocationPermissions()
		if (hasLocationPermissions) {
			checkLocationSettings()
		}

		// Setup the date picker
		datePicker.addOnPositiveButtonClickListener { dateSelected ->
			// Set the time in the date EditText
			binding.date.setText(
				formatDateFromMillis(dateSelected)
			)
			millis = dateSelected
			onInputChange()
		}

		// Setup the time picker
		timePicker.addOnPositiveButtonClickListener {
			// Set the time in the time EditText
			binding.time.setText(
				formatTime(timePicker.hour, timePicker.minute)
			)
			hour = timePicker.hour
			minute = timePicker.minute
			onInputChange()
		}

		binding.apply {
			dataViewModel = sharedViewModel
			lifecycleOwner = viewLifecycleOwner

			sharedViewModel.restartSearch()

			date.showSoftInputOnFocus = false
			date.setOnClickListener {
				hideKeyboard()
				showDatePicker()
			}

			time.showSoftInputOnFocus = false
			time.setOnClickListener {
				hideKeyboard()
				showTimePicker()
			}

			source.setOnClickListener {
				goToSearchAddress("source")
			}
			destination.setOnClickListener {
				goToSearchAddress("destination")
			}

			// Filter the search results according to
			// the selected chip
			chipClassic.setOnClickListener {
				sharedViewModel.setSearchedRidesFilter(
					WheelsType.CLASSIC_WHEELS
				)
			}
			chipShared.setOnClickListener {
				sharedViewModel.setSearchedRidesFilter(
					WheelsType.SHARED_WHEELS
				)
			}
			chipWe.setOnClickListener {
				sharedViewModel.setSearchedRidesFilter(
					WheelsType.WE_WHEELS
				)
			}

			request.setOnClickListener {
				onRequestPressed()
			}

			// Set the adapter for the recycler view
			searchResults.adapter = SearchedRideItemAdapter(
				onItemClicked = {
					onRideSelected(it)
				},
				query = sharedViewModel.query
			)
		}

		viewModel.route.observe(viewLifecycleOwner) {
			drawRoute(it)
		}
		viewModel.sourceAddress.observe(viewLifecycleOwner) {
			binding.source.setText(it?.mainText)
			drawSourceMarker(it?.latLng?.toLatLng())
		}
		viewModel.destinationAddress.observe(viewLifecycleOwner) {
			binding.destination.setText(it?.mainText)
			drawDestinationMarker(it?.latLng?.toLatLng())
		}
		sharedViewModel.selectedSearchedRide.observe(viewLifecycleOwner) { }
		sharedViewModel.requestResult.observe(viewLifecycleOwner) { result ->
			result ?: return@observe
			result.message?.let {
				showMessage(it)
			}
			if (result.success) {
				onRideRequested()
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onResume() {
		super.onResume()

		if (DEBUG_USE_SENSORS) {
			mSensorManager.registerListener(
				lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL
			)
		}
		if (hasLocationPermissions && locationSettingsEnabled) {
			startLocationUpdates()
		}
	}

	override fun onPause() {
		super.onPause()

		if (DEBUG_USE_SENSORS) {
			mSensorManager.unregisterListener(lightEventListener)
		}
		stopLocationUpdates()
	}

	private fun goToSearchAddress(selectedInput: String) {
		val action =
			RequestRideFragmentDirections
				.actionRequestRideFragmentToSearchAddressFragment(
					selectedInput = selectedInput,
					source = viewModel.sourceAddress.value,
					destination = viewModel.destinationAddress.value
				)

		findNavController()
			.navigate(action)
	}

	private fun onInputChange() {
		if (
			millis != null && hour != null && minute != null &&
			viewModel.sourceAddress.value != null &&
			viewModel.destinationAddress.value != null
		) {
			sharedViewModel.searchRides(
				source = viewModel.sourceAddress.value!!,
				destination = viewModel.destinationAddress.value!!,
				date = CustomDate(
					millis, hour, minute
				)
			)

			// Set the adapter for the recycler view
			binding.searchResults.adapter = SearchedRideItemAdapter(
				onItemClicked = {
					onRideSelected(it)
				},
				query = sharedViewModel.query
			)
		}
	}

	private fun onRideSelected(ride: Ride) {
		sharedViewModel.selectSearchedRide(ride.id)
	}

	private fun onRequestPressed() {
		sharedViewModel.requestRide()
	}

	private fun onRideRequested() {
		AlertDialog.Builder(requireContext())
			.setTitle(R.string.ride_requested)
			.setMessage(R.string.ride_requested_message)
			.setPositiveButton(R.string.ok) { _, _ -> }
			.show()
	}

	private val datePicker =
		MaterialDatePicker.Builder
			.datePicker()
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
					mMap?.moveCamera(
						CameraUpdateFactory.newLatLngZoom(
							LatLng(location.latitude, location.longitude),
							15f
						)
					)
				}
			}
			.addOnFailureListener {
				Log.w(TAG, "Failed to get last known location")
				Log.w(TAG, it.message ?: "")

				moveCameraToDefault()
			}
			.addOnCanceledListener {
				Log.w(TAG, "Last known location task was cancelled")
			}

		if (hasLocationPermissions) {
			mMap!!.isMyLocationEnabled = true
		}
	}

	private fun moveCameraToDefault() {
		mMap!!.moveCamera(
			CameraUpdateFactory.newLatLngZoom(
				LatLng(BOGOTA_LAT, BOGOTA_LNG),
				BOGOTA_ZOOM
			)
		)
	}

	private fun moveAndZoomToLocation(location: LatLng, zoomLevel: Float = DEFAULT_ZOOM_LEVEL) {
		mMap?.moveCamera(CameraUpdateFactory.newLatLng(location))
		mMap?.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
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

	private fun createLightEventListener(): SensorEventListener {
		return object : SensorEventListener {
			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
			}

			override fun onSensorChanged(event: SensorEvent?) {
				if (event != null) {
					val lux = event.values[0]
					if (lux < LUX_THRESHOLD) {
						mMap?.setMapStyle(
							MapStyleOptions.loadRawResourceStyle(
								requireContext(), R.raw.dark_map
							)
						)
					} else {
						mMap?.setMapStyle(
							MapStyleOptions.loadRawResourceStyle(
								requireContext(), R.raw.light_map
							)
						)
					}
				}
			}
		}
	}

	private fun createLocationRequest(): LocationRequest {
		return LocationRequest.create().apply {
			interval = LOCATION_REQUEST_INTERVAL
			fastestInterval = LOCATION_REQUEST_FAST_INTERVAL
			priority = LocationRequest.PRIORITY_HIGH_ACCURACY
		}
	}

	private fun createLocationCallback(): LocationCallback {
		return object : LocationCallback() {
			override fun onLocationResult(locationResult: LocationResult) {
				locationResult.lastLocation.also {
					val latLng = LatLng(it.latitude, it.longitude)
					Log.d(TAG, "New user location: $latLng at ${it.time}")

					// sharedViewModel.updateUserLocation(latLng)
					// drawUserLocationMarker(latLng)
				}
			}
		}
	}

	@SuppressLint("MissingPermission")
	private fun startLocationUpdates() {
		// fusedLocationClient.requestLocationUpdates(
		// 	locationRequest,
		// 	locationCallback,
		// 	Looper.getMainLooper()
		// )
	}

	private fun stopLocationUpdates() {
		// fusedLocationClient.removeLocationUpdates(locationCallback)
		// sharedViewModel.updateUserLocation(null)
		// drawUserLocationMarker(null)
		// drawRoute(null)
	}

	// PERMISSIONS BOILERPLATE

	/**
	 * Checks the location permissions and requests them if they are not granted.
	 */
	private fun checkLocationPermissions() {
		when (PackageManager.PERMISSION_GRANTED) {
			activity?.checkSelfPermission(
				Manifest.permission.ACCESS_FINE_LOCATION
			) -> {
				onPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
			}
			activity?.checkSelfPermission(
				Manifest.permission.ACCESS_COARSE_LOCATION
			) -> {
				onPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
				requestPermissionsLocation()    // To upgrade to fine location access
			}
			else -> {
				requestPermissionsLocation()
			}
		}
	}

	/**
	 * Requests all location permissions (FINE and COARSE).
	 */
	private fun requestPermissionsLocation() {
		locationPermissionsRequester.launch(
			arrayOf(
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
			)
		)
	}

	/**
	 * Launches an implicit intent to request the location access permissions.
	 * - If the permission to access fine location is granted,
	 * [onPermissionGranted] with ACCESS_FINE_LOCATION is called.
	 * - If the permission to access coarse location is granted,
	 * [onPermissionGranted] with ACCESS_COARSE_LOCATION is called.
	 * - If the permissions are denied, it may show the rationale
	 * calling [requestPermissionRationale].
	 * - If the permissions are definitely denied,
	 * [onPermissionsDenied] is called.
	 */
	private val locationPermissionsRequester =
		registerForActivityResult(
			ActivityResultContracts.RequestMultiplePermissions()
		) { map ->

			when {
				map[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
					onPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
				}

				map[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
					onPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
				}

				shouldShowRequestPermissionRationale(
					Manifest.permission.ACCESS_FINE_LOCATION
				) -> {
					if (map.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION))
						requestPermissionRationale(true)
					else
						requestPermissionRationale(false)
				}

				else -> onPermissionsDenied()
			}
		}

	/**
	 * Callback function if any permission to access location is granted.
	 * Sets the granted flags in the view model
	 */
	private fun onPermissionGranted(permission: String) {
		when (permission) {
			Manifest.permission.ACCESS_FINE_LOCATION -> {
				hasLocationPermissions = true
			}
			Manifest.permission.ACCESS_COARSE_LOCATION -> {
				hasLocationPermissions = true
			}
		}
	}

	/**
	 * Callback function if permissions to access location are denied.
	 * Sets the denied flags in the view model
	 */
	private fun onPermissionsDenied() {
		if (!hasLocationPermissions) {
			hasLocationPermissions = false
		}
	}

	/**
	 * Callback function if the user has denied the permission to access location
	 * and the rationale should be shown.
	 * Shows the rationale dialog.
	 */
	private fun requestPermissionRationale(both: Boolean) {
		val title: String
		val message: String
		when {
			both -> {
				title = getString(R.string.permissions_location_title)
				message = getString(R.string.permissions_location_message)
			}
			else -> {
				title = getString(R.string.permission_fine_location_title)
				message = getString(R.string.permission_fine_location_message)
			}
		}

		AlertDialog.Builder(requireContext())
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(R.string.request_permission) { _, _ ->
				requestPermissionsLocation()
			}
			.setNegativeButton(R.string.dismiss) { _, _ ->
				onPermissionsDenied()
			}
			.show()
	}

	private fun checkLocationSettings() {
		LocationSettingsRequest.Builder()
			.addLocationRequest(locationRequest)
			.build()
			.let { it ->
				LocationServices.getSettingsClient(requireActivity())
					.checkLocationSettings(it)
					.addOnSuccessListener {
						onLocationSettingsEnabled()
					}
					.addOnFailureListener {
						if (it is ResolvableApiException) {
							val isr = IntentSenderRequest
								.Builder(it.resolution)
								.build()
							getLocationSettings.launch(isr)
						} else {
							onLocationSettingsUnavailable()
						}
					}
			}
	}

	private val getLocationSettings =
		registerForActivityResult(
			ActivityResultContracts.StartIntentSenderForResult()
		) {
			if (it.resultCode == Activity.RESULT_OK) {
				onLocationSettingsEnabled()
			} else {
				onLocationSettingsUnavailable()
			}
		}

	private fun onLocationSettingsEnabled() {
		locationSettingsEnabled = true
	}

	private fun onLocationSettingsUnavailable() {
		locationSettingsEnabled = false
	}
}
