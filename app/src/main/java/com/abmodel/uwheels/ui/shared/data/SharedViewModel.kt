package com.abmodel.uwheels.ui.shared.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.data.repository.ride.FirebaseRideRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class SharedViewModel: ViewModel() {

	companion object {
		const val TAG = "SharedViewModel"
	}

	private val authRepository = FirebaseAuthRepository.getInstance()
	private val rideRepository = FirebaseRideRepository.getInstance()

	private val _userRides = MutableLiveData<List<Ride>>()
	val userRides
		get() = _userRides

	init {
		Log.d(TAG, "SharedViewModel initialized")
		// fetch user rides forever
		fetchUserRides()
	}

	private fun fetchUserRides() {
		viewModelScope.launch(Dispatchers.IO) {

			rideRepository.fetchUserRides(authRepository.getLoggedInUser().uid).collect { result ->

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
