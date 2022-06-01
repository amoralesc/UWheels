package com.abmodel.uwheels.ui.shared.data

import android.util.Log
import androidx.lifecycle.*
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.data.model.RideStatus
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

	private val _selectedRidesPage: MutableLiveData<RidesPage> = MutableLiveData(RidesPage.ACTIVE)
	val selectedRidesPage: LiveData<RidesPage>
		get() = _selectedRidesPage

	val selectedRides: LiveData<List<Ride>> =
		Transformations.switchMap(_selectedRidesPage) { page ->
			when (page) {
				RidesPage.ACTIVE -> activeRides
				RidesPage.REQUESTED -> requestedRides
				RidesPage.COMPLETED -> completedRides
				else -> hostedRides // RidesPage.HOSTED
			}
		}

	private var fetchUserRidesJob: Job? = null

	init {
		Log.d(TAG, "SharedViewModel initialized")

		if (authRepository.isDriverModeOn()) {
			fetchUserRides(true)
		} else {
			fetchUserRides()
		}
	}

	fun setSelectedRidesPage(page: RidesPage) {
		_selectedRidesPage.postValue(page)
	}

	private fun fetchUserRides(hostedOnly: Boolean = false) {
		fetchUserRidesJob =
			viewModelScope.launch(Dispatchers.IO) {

				ridesRepository.fetchUserRides(
					authRepository.getLoggedInUser().uid,
					hosted = hostedOnly
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

	fun stopFetchingUserRides() {
		fetchUserRidesJob?.cancel()
	}
}
