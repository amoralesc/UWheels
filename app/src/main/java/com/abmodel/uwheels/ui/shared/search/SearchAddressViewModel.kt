package com.abmodel.uwheels.ui.shared.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.abmodel.uwheels.BuildConfig.MAPS_API_KEY
import com.abmodel.uwheels.network.maps.GeocodingApi
import com.abmodel.uwheels.network.maps.GeocodingResponse
import com.abmodel.uwheels.network.maps.PlacesAutocompleteApi
import com.abmodel.uwheels.network.maps.PlacesAutocompleteResponse
import com.abmodel.uwheels.util.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchAddressViewModel(
	application: Application
) : AndroidViewModel(application) {

	private val _addressResults = MutableLiveData<List<CustomAddress>>()
	val addressResults: MutableLiveData<List<CustomAddress>>
		get() = _addressResults

	private var autocompleteSessionToken = AutocompleteSessionToken.newInstance()

	private val _sourceAddress = MutableLiveData<CustomAddress>()
	val sourceAddress: LiveData<CustomAddress>
		get() = _sourceAddress

	private val _destinationAddress = MutableLiveData<CustomAddress>()
	val destinationAddress: LiveData<CustomAddress>
		get() = _destinationAddress

	fun autocompleteAddress(query: String) {
		if (query.length < 4) return

		viewModelScope.launch(Dispatchers.IO) {
			try {
				val response: PlacesAutocompleteResponse =
					PlacesAutocompleteApi.retrofitService.getAutocomplete(
						query,
						parseLatLng(LatLng(BOGOTA_LAT, BOGOTA_LNG)),
						AUTOCOMPLETE_DEFAULT_RADIUS,
						AUTOCOMPLETE_DEFAULT_REGION,
						autocompleteSessionToken.toString(),
						MAPS_API_KEY
					)

				val addresses = mutableListOf<CustomAddress>()
				if (response.predictions.isNotEmpty()) {
					for (prediction in response.predictions) {
						addresses.add(
							CustomAddress(
								prediction.placeId!!,
								prediction.structuredFormatting.mainText,
								prediction.structuredFormatting.secondaryText,
								null
							)
						)
					}
				}
				_addressResults.postValue(addresses)

			} catch (e: Exception) {
				Log.e(SearchAddressFragment.TAG, "Error fetching autocomplete: ${e.message}")
			}
		}
	}

	fun updateSourceAddress(address: CustomAddress) {
		_sourceAddress.postValue(address)
	}

	fun updateDestinationAddress(address: CustomAddress) {
		_destinationAddress.postValue(address)
	}

	fun selectAddress(address: CustomAddress, selectedInput: String) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val response: GeocodingResponse =
					GeocodingApi.retrofitService.getGeocoding(
						address.placeId,
						MAPS_API_KEY
					)

				if (response.results.isNotEmpty()) {
					val result = response.results[0]

					val resultAddress = CustomAddress(
						address.placeId,
						address.mainText,
						address.secondaryText,
						LatLng(
							result.geometry.location.lat,
							result.geometry.location.lng
						)
					)

					when (selectedInput) {
						"source" -> _sourceAddress.postValue(resultAddress)
						"destination" -> _destinationAddress.postValue(resultAddress)
					}

					autocompleteSessionToken = AutocompleteSessionToken.newInstance()
				}
			} catch (e: Exception) {
				Log.e(SearchAddressFragment.TAG, "Error fetching geocoding: ${e.message}")
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
