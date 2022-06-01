package com.abmodel.uwheels.ui.passenger.ride.create

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abmodel.uwheels.BuildConfig
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.*
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.data.repository.ride.FirebaseRideRepository
import com.abmodel.uwheels.network.maps.DirectionsApi
import com.abmodel.uwheels.network.maps.response.DirectionsResponse
import com.abmodel.uwheels.network.maps.response.DirectionsRoute
import com.abmodel.uwheels.ui.passenger.ride.request.RequestRideFragment
import com.abmodel.uwheels.ui.shared.data.FormResult
import com.abmodel.uwheels.util.PolyUtil
import com.abmodel.uwheels.util.compareDates
import com.abmodel.uwheels.util.getCurrentDateAsCustomDate
import com.abmodel.uwheels.util.parseLatLng
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PassengerCreateRideViewModel : ViewModel() {

	private val repository = FirebaseRideRepository.getInstance()

	private val _sourceAddress = MutableLiveData<CustomAddress?>(null)
	val sourceAddress: MutableLiveData<CustomAddress?>
		get() = _sourceAddress

	private val _destinationAddress = MutableLiveData<CustomAddress?>(null)
	val destinationAddress: MutableLiveData<CustomAddress?>
		get() = _destinationAddress

	private val _route = MutableLiveData<List<LatLng>?>(null)
	val route: MutableLiveData<List<LatLng>?>
		get() = _route

	private val _date = MutableLiveData<CustomDate>()
	val date: MutableLiveData<CustomDate>
		get() = _date

	private val _result = MutableLiveData<FormResult>()
	val result: MutableLiveData<FormResult>
		get() = _result

	init {
		_result.postValue(FormResult())
		_date.postValue(CustomDate())
	}

	fun updateSourceAddress(address: CustomAddress?) {
		if (address != null) {
			_sourceAddress.value = address
			calculateRoute()
		}
	}

	fun updateDestinationAddress(address: CustomAddress?) {
		if (address != null) {
			_destinationAddress.value = address
			calculateRoute()
		}
	}

	private fun calculateRoute() {
		if (_sourceAddress.value != null &&
			_destinationAddress.value != null
		) {
			viewModelScope.launch(Dispatchers.IO) {
				try {
					val response: DirectionsResponse =
						DirectionsApi.retrofitService.getDirections(
							parseLatLng(sourceAddress.value!!.latLng!!),
							parseLatLng(destinationAddress.value!!.latLng!!),
							"co",
							BuildConfig.MAPS_API_KEY
						)

					if (response.routes?.isNotEmpty() == true) {
						val route: DirectionsRoute = response.routes[0]

						val points: MutableList<LatLng> = mutableListOf()
						route.legs.forEach { leg ->
							leg.steps.forEach { step ->
								points.addAll(
									PolyUtil.decode(step.polyline.points)
								)
							}
						}

						_route.postValue(points)
					}
				} catch (e: Exception) {
					Log.e(RequestRideFragment.TAG, "Error calculating route", e)
				}
			}
		} else {
			_route.value = null
		}
	}

	fun updateDate(
		millis: Long? = null, hour: Int? = null, minute: Int? = null
	) {
		millis?.let {
			_date.value!!.millis = it
		}
		hour?.let {
			_date.value!!.hour = it
		}
		minute?.let {
			_date.value!!.minute = it
		}
	}

	fun createRide(
		wheelsType: String, transportation: String, capacity: Int
	) {
		viewModelScope.launch(Dispatchers.Main) {
			if (validateForm()) {
				val user = FirebaseAuthRepository.getInstance().getLoggedInUser()

				val ride = Ride(
					source = sourceAddress.value!!,
					destination = destinationAddress.value!!,
					date = _date.value!!,
					wheelsType = wheelsType,
					transportation = transportation,
					totalCapacity = capacity,
					rating = user.passengerRating,
					host = RideUser(
						user.uid,
						user.name,
						user.lastName,
						user.passengerRating,
						user.photoUrl.toString()
					),
					state = RideState.CREATED.toString(),
					subscribers = mutableListOf(user.uid)
				)
				repository.createRide(ride)

				_result.postValue(FormResult(success = true, message = R.string.ride_created))
			}
		}
	}

	private fun validateForm(): Boolean {

		return when {
			sourceAddress.value == null -> {
				_result.postValue(FormResult(message = R.string.source_address_empty))
				false
			}
			destinationAddress.value == null -> {
				_result.postValue(FormResult(message = R.string.destination_address_empty))
				false
			}
			date.value == null -> {
				_result.postValue(FormResult(message = R.string.date_empty))
				false
			}
			date.value!!.millis == null ||
					date.value!!.hour == null ||
					date.value!!.minute == null -> {
				_result.postValue(FormResult(message = R.string.date_empty))
				false
			}
			date.value!!.compareDates(
				getCurrentDateAsCustomDate()
			) < 0 -> {
				_result.postValue(FormResult(message = R.string.date_in_past))
				false
			}
			else -> {
				_result.postValue(FormResult(message = R.string.creating_ride))
				true
			}
		}
	}
}