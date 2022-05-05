package com.abmodel.uwheels.network.maps

import android.gesture.Prediction
import com.google.maps.android.data.Geometry
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

private val moshi = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
	.build()

private val retrofit = Retrofit.Builder()
	.addConverterFactory(MoshiConverterFactory.create(moshi))
	.baseUrl(BASE_URL)
	.build()

interface DirectionsApiService {
	@GET("directions/json")
	suspend fun getDirections(
		@Query("origin") origin: String,
		@Query("destination") destination: String,
		@Query("region") region: String,
		@Query("sessiontoken") sessionToken: String,
		@Query("key") key: String
	): DirectionsResponse
}

object DirectionsApi {
	val retrofitService: DirectionsApiService by lazy {
		retrofit.create(DirectionsApiService::class.java)
	}
}

interface PlacesAutocompleteApiService {
	@GET("place/autocomplete/json")
	suspend fun getAutocomplete(
		@Query("input") input: String,
		@Query("location") location: String,
		@Query("radius") radius: Int,
		@Query("region") region: String,
		@Query("key") key: String
	): PlacesAutocompleteResponse
}

object PlacesAutocompleteApi {
	val retrofitService: PlacesAutocompleteApiService by lazy {
		retrofit.create(PlacesAutocompleteApiService::class.java)
	}
}

interface GeocodingApiService {
	@GET("geocode/json")
	suspend fun getGeocoding(
		@Query("place_id") placeId: String,
		@Query("key") key: String
	): GeocodingResponse
}

object GeocodingApi {
	val retrofitService: GeocodingApiService by lazy {
		retrofit.create(GeocodingApiService::class.java)
	}
}