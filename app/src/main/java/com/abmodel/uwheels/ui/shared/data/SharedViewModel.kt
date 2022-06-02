package com.abmodel.uwheels.ui.shared.data

import android.util.Log
import androidx.lifecycle.*
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.*
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.data.repository.ride.FirebaseRidesRepository
import com.abmodel.uwheels.util.difference
import com.abmodel.uwheels.util.distanceTo
import com.abmodel.uwheels.util.getCurrentDateAsCustomDate
import com.abmodel.uwheels.util.toLatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.Normalizer

class SharedViewModel : ViewModel() {

	companion object {
		const val TAG = "SharedViewModel"
	}

	private val authRepository = FirebaseAuthRepository.getInstance()
	private val ridesRepository = FirebaseRidesRepository.getInstance()

	// TODO: TOO MUCH BOILERPLATE CODE!!!

	private val _userRides = MutableLiveData<List<Ride>>(emptyList())

	private val activeUserRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.status != RideStatus.COMPLETED.toString() &&
						ride.status != RideStatus.CANCELLED.toString() &&
						ride.requests.none { request ->
							request.user.uid == authRepository.getLoggedInUser().uid
						}
			}
			MutableLiveData(filteredRides)
		}

	private val hostedUserRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.host.uid == authRepository.getLoggedInUser().uid &&
						ride.status != RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val requestedUserRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.requests.any { request ->
					request.user.uid == authRepository.getLoggedInUser().uid
				}
			}
			MutableLiveData(filteredRides)
		}

	private val completedUserRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.status == RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val _userRidesFilter = MutableLiveData(UserRidesFilter.ACTIVE)
	val userRidesFilter: LiveData<UserRidesFilter>
		get() = _userRidesFilter

	val filteredUserRides: LiveData<List<Ride>> =
		Transformations.switchMap(_userRidesFilter) { filter ->
			when (filter) {
				UserRidesFilter.ACTIVE -> activeUserRides
				UserRidesFilter.REQUESTED -> requestedUserRides
				UserRidesFilter.COMPLETED -> completedUserRides
				UserRidesFilter.HOSTED -> hostedUserRides
				else -> null
			}
		}

	private val _selectedUserRideId: MutableLiveData<String> = MutableLiveData()
	private val selectedUserRideId: LiveData<String>
		get() = _selectedUserRideId

	val selectedUserRide: LiveData<Ride> =
		Transformations.switchMap(filteredUserRides) { rides ->
			Transformations.switchMap(_selectedUserRideId) { rideId ->
				MutableLiveData(rides.find { ride ->
					ride.id == rideId
				})
			}
		}

	private val _searchedRides = MutableLiveData<List<Ride>>(emptyList())

	private val classicWheelsSearchedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_searchedRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.wheelsType == WheelsType.CLASSIC_WHEELS.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val sharedWheelsSearchedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_searchedRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.wheelsType == WheelsType.SHARED_WHEELS.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val weWheelsSearchedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_searchedRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.wheelsType == WheelsType.WE_WHEELS.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val _searchedRidesFilter = MutableLiveData(WheelsType.CLASSIC_WHEELS)
	val searchedRidesFilter: LiveData<WheelsType>
		get() = _searchedRidesFilter

	val filteredSearchedRides: LiveData<List<Ride>> =
		Transformations.switchMap(_searchedRidesFilter) { filter ->
			when (filter) {
				WheelsType.CLASSIC_WHEELS -> classicWheelsSearchedRides
				WheelsType.SHARED_WHEELS -> sharedWheelsSearchedRides
				WheelsType.WE_WHEELS -> weWheelsSearchedRides
				else -> null
			}
		}

	private val _selectedSearchedRideId: MutableLiveData<String?> = MutableLiveData()
	val selectedSearchedRideId: LiveData<String?>
		get() = _selectedSearchedRideId

	val selectedSearchedRide: MediatorLiveData<Ride?> =
		MediatorLiveData<Ride?>().apply {
			addSource(selectedSearchedRideId) {
				val ride = filteredSearchedRides.value?.find { ride ->
					ride.id == it
				}
				value = ride
			}
		}

	private val _requestResult = MutableLiveData<FormResult>()
	val requestResult: LiveData<FormResult>
		get() = _requestResult

	private var fetchUserRidesJob: Job? = null
	private var fetchSearchedRidesJob: Job? = null

	private var _query: SearchRideQuery? = null
	val query: SearchRideQuery?
		get() = _query

	init {
		Log.d(TAG, "SharedViewModel initialized")

		if (authRepository.isDriverModeOn()) {
			fetchUserRides(true, WheelsType.CLASSIC_WHEELS)
		} else {
			fetchUserRides()
		}

		restartSearch()
	}

	fun setUserRidesFilter(filter: UserRidesFilter) {
		_userRidesFilter.postValue(filter)
	}

	fun selectUserRide(rideId: String) {
		_selectedUserRideId.postValue(rideId)
	}

	fun setSearchedRidesFilter(filter: WheelsType) {
		_searchedRidesFilter.postValue(filter)
	}

	fun selectSearchedRide(rideId: String) {
		_selectedSearchedRideId.value = rideId
	}

	fun acceptRideRequest(request: RideRequest) {
		viewModelScope.launch(Dispatchers.IO) {
			ridesRepository.acceptRideRequest(selectedUserRideId.value!!, request)
		}
	}

	fun rejectRideRequest(request: RideRequest) {
		viewModelScope.launch(Dispatchers.IO) {
			ridesRepository.rejectRideRequest(selectedUserRideId.value!!, request)
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

	fun requestRide() {
		viewModelScope.launch(Dispatchers.IO) {
			if (selectedSearchedRideId.value == null ||
				selectedSearchedRide.value == null ||
				query == null
			) {
				return@launch
			}

			_requestResult.postValue(
				FormResult(message = R.string.requesting_ride)
			)

			ridesRepository.requestRide(
				selectedSearchedRideId.value!!,
				RideRequest(
					user = RideUser.fromLoggedInUser(
						authRepository.getLoggedInUser()
					),
					sentDate = getCurrentDateAsCustomDate(),
					source = query!!.source,
					destination = query!!.destination,
					sourceDistance = query!!.source.latLng!!.toLatLng().distanceTo(
						selectedSearchedRide.value!!.source.latLng!!.toLatLng()
					),
					destinationDistance = query!!.destination.latLng!!.toLatLng().distanceTo(
						selectedSearchedRide.value!!.destination.latLng!!.toLatLng()
					),
					date = query!!.date,
					dateDifference = query!!.date.difference(
						selectedSearchedRide.value!!.date
					)
				)
			)

			_requestResult.postValue(
				FormResult(success = true, message = R.string.request_sent)
			)
			_selectedSearchedRideId.postValue(null)
			selectedSearchedRide.postValue(null)
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

	fun searchRides(
		source: CustomAddress, destination: CustomAddress, date: CustomDate,
	) {
		_query = SearchRideQuery(
			userId = authRepository.getLoggedInUser().uid,
			source = source,
			destination = destination,
			date = date
		)
		fetchSearchedRidesJob?.cancel()
		fetchSearchedRides(query!!)
	}

	private fun fetchSearchedRides(query: SearchRideQuery) {
		fetchSearchedRidesJob =
			viewModelScope.launch(Dispatchers.IO) {
				ridesRepository.searchRides(query).cancellable().collect { result ->

					if (result.isSuccess) {
						_searchedRides.postValue(result.getOrNull())
						Log.d(TAG, "fetchSearchedRides: ${result.getOrNull()}")
					} else {
						Log.e(TAG, "Error: ${result.exceptionOrNull()}")
					}
				}
			}
	}

	fun restartSearch() {
		_query = null
		fetchSearchedRidesJob?.cancel()
		_searchedRides.postValue(emptyList())
		_selectedSearchedRideId.postValue("")
		_requestResult.postValue(FormResult())
	}
}
