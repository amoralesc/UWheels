package com.abmodel.uwheels.ui.passenger.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentSearchRideBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class SearchRideFragment : Fragment(), OnMapReadyCallback {

	// Binding objects to access the view elements
	private var _binding: FragmentSearchRideBinding? = null
	private val binding get() = _binding!!

	private var mMap: GoogleMap? = null

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentSearchRideBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Obtain the SupportMapFragment and get notified
		// when the map is ready to be used.
		val mapFragment = childFragmentManager
			.findFragmentById(R.id.mapSearchRide) as? SupportMapFragment
		mapFragment?.getMapAsync(this)
	}

	/**
	 * Manipulates the map once available. The callback is triggered
	 * when the map is ready to be used.
	 */
	override fun onMapReady(googleMap: GoogleMap) {
		mMap = googleMap
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
