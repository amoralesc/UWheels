package com.abmodel.uwheels.ui.shared.data

import android.util.Log
import androidx.lifecycle.*
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.data.model.RideRequest
import com.abmodel.uwheels.data.model.RideStatus
import com.abmodel.uwheels.data.model.WheelsType
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.data.repository.ride.FirebaseRidesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

	companion object {
		const val TAG = "SharedViewModel"
	}

	private val authRepository = FirebaseAuthRepository.getInstance()
	private val ridesRepository = FirebaseRidesRepository.getInstance()

	private val _userRides = MutableLiveData<List<Ride>>()

	private val activeRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.status != RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val hostedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.host.uid == authRepository.getLoggedInUser().uid &&
						ride.status != RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val requestedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.requests.any { request ->
					request.user.uid == authRepository.getLoggedInUser().uid
				}
			}
			MutableLiveData(filteredRides)
		}

	private val completedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.status == RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val _ridesFilter: MutableLiveData<RidesFilter> = MutableLiveData(RidesFilter.ACTIVE)
	val ridesFilter: LiveData<RidesFilter>
		get() = _ridesFilter

	val filteredRides: LiveData<List<Ride>> =
		Transformations.switchMap(_ridesFilter) { filter ->
			when (filter) {
				RidesFilter.ACTIVE -> activeRides
				RidesFilter.REQUESTED -> requestedRides
				RidesFilter.COMPLETED -> completedRides
				RidesFilter.HOSTED -> hostedRides
				else -> null
			}
		}

	private val _selectedRideId: MutableLiveData<String> = MutableLiveData()
	val selectedRideId: LiveData<String>
		get() = _selectedRideId

	val selectedRide: LiveData<Ride> =
		Transformations.switchMap(filteredRides) { rides ->
			Transformations.switchMap(_selectedRideId) { rideId ->
				MutableLiveData(rides.find { ride ->
					ride.id == rideId
				})
			}
		}

	private var fetchUserRidesJob: Job? = null

	init {
		Log.d(TAG, "SharedViewModel initialized")

		if (authRepository.isDriverModeOn()) {
			fetchUserRides(true, WheelsType.CLASSIC_WHEELS)
		} else {
			fetchUserRides()
		}
	}

	fun setRidesFilter(filter: RidesFilter) {
		_ridesFilter.postValue(filter)
	}

	fun selectRide(rideId: String) {
		_selectedRideId.postValue(rideId)
	}

	fun acceptRideRequest(request: RideRequest) {

		viewModelScope.launch(Dispatchers.IO) {
			ridesRepository.acceptRideRequest(selectedRideId.value!!, request)
		}
	}

	fun rejectRideRequest(request: RideRequest) {

		viewModelScope.launch(Dispatchers.IO) {
			ridesRepository.rejectRideRequest(selectedRideId.value!!, request)
		}
	}

	private fun fetchUserRides(
		hostedOnly: Boolean = false,
		wheelsType: WheelsType? = null
	) {

		fetchUserRidesJob =
			viewModelScope.launch(Dispatchers.IO) {

				ridesRepository.fetchUserRides(
					authRepository.getLoggedInUser().uid,
					hosted = hostedOnly,
					wheelsType = wheelsType
				).cancellable().collect { result ->

					if (result.isSuccess) {
						_userRides.postValue(result.getOrNull())
						Log.d(TAG, "fetchUserRides: ${result.getOrNull()}")
					} else {
						Log.e(TAG, "Error: ${result.exceptionOrNull()}")
					}
				}
			}
	}

	fun driverModeChanged(driverMode: Boolean) {
		fetchUserRidesJob?.cancel()

		if (driverMode) {
			fetchUserRides(true, WheelsType.CLASSIC_WHEELS)
		} else {
			fetchUserRides()
		}
	}
}
