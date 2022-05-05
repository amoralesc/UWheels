package com.abmodel.uwheels.network.maps

import com.squareup.moshi.Json

data class PlaceAutocompleteMatchedSubstring(
	@Json(name = "length") val length: Int,
	@Json(name = "offset") val offset: Int
)

data class PlaceAutocompleteStructuredFormat(
	@Json(name = "main_text") val mainText: String,
	@Json(name = "main_text_matched_substrings") val mainTextMatchedSubstrings: List<PlaceAutocompleteMatchedSubstring>,
	@Json(name = "secondary_text") val secondaryText: String,
	@Json(name = "secondary_text_matched_substrings") val secondaryTextMatchedSubstrings: List<PlaceAutocompleteMatchedSubstring>?
)

data class PlaceAutocompleteTerm(
	@Json(name = "offset") val offset: Int,
	@Json(name = "value") val value: String
)

data class PlaceAutocompletePrediction(
	@Json(name = "description") val description: String,
	@Json(name = "matched_substrings") val matchedSubstrings: List<PlaceAutocompleteMatchedSubstring>,
	@Json(name = "structured_formatting") val structuredFormatting: PlaceAutocompleteStructuredFormat,
	@Json(name = "terms") val terms: List<PlaceAutocompleteTerm>,
	@Json(name = "place_id") val placeId: String?,
	@Json(name = "types") val types: List<String>?
)

data class PlacesAutocompleteStatus(
	val status: String
)

data class PlacesAutocompleteResponse(
	@Json(name = "predictions") val predictions: List<PlaceAutocompletePrediction>,
	@Json(name = "status") val status: PlacesAutocompleteStatus,
	@Json(name = "error_message") val errorMessage: String?,
	@Json(name = "info_messages") val infoMessages: List<String>?
)
