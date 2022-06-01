package com.abmodel.uwheels.ui.shared.data

import android.util.Log
import androidx.lifecycle.*
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.data.model.RideStatus
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.data.repository.ride.FirebaseRidesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

	companion object {
		const val TAG = "SharedViewModel"
	}

	private val authRepository = FirebaseAuthRepository.getInstance()
	private val ridesRepository = FirebaseRidesRepository.getInstance()

	private val _userRides = MutableLiveData<List<Ride>>()

	val hostedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.host.uid == authRepository.getLoggedInUser().uid &&
						ride.status != RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	val completedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.status == RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	val activeRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.status != RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	init {
		Log.d(TAG, "SharedViewModel initialized")
		// fetch user rides forever
		fetchUserRides()
	}

	private fun fetchUserRides() {
		viewModelScope.launch(Dispatchers.IO) {

			ridesRepository.fetchUserRides(authRepository.getLoggedInUser().uid).collect { result ->

				if (result.isSuccess) {
					_userRides.postValue(result.getOrNull())
					Log.d(TAG, "fetchUserRides: ${result.getOrNull()}")
				} else {
					Log.e(TAG, "Error: ${result.exceptionOrNull()}")
				}
			}
		}
	}
}
