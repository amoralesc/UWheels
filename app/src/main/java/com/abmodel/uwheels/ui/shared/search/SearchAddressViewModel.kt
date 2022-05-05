package com.abmodel.uwheels.ui.shared.search

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.*
import com.abmodel.uwheels.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchAddressViewModel(
	application: Application
) : AndroidViewModel(application) {

	private val _addressResults = MutableLiveData<List<Address>>()
	val addressResults: MutableLiveData<List<Address>>
		get() = _addressResults

	val mGeocoder: Geocoder = Geocoder(getApplication())

	fun searchAddress(query: String) {
		viewModelScope.launch(Dispatchers.IO) {
			//runCatching {
			try {
				val results = mGeocoder.getFromLocationName(
					query, GEOCODER_MAX_RESULTS,
					LOWER_LEFT_LATITUDE, LOWER_LEFT_LONGITUDE,
					UPPER_RIGHT_LATITUDE, UPPER_RIGHT_LONGITUDE
				)

				if (results != null && results.isNotEmpty()) {
					Log.d(SearchAddressFragment.TAG, "Results: $results")
					_addressResults.postValue(results)
				}
			//}
			} catch (e: Exception) {
				Log.e(SearchAddressFragment.TAG, "Error: ${e.message}")
			}
		}
	}
}

class SearchAddressViewModelFactory(
	private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {
	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(SearchAddressViewModel::class.java)) {
			return SearchAddressViewModel(application) as T
		} else {
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
}
