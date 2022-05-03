package com.abmodel.uwheels.ui.passenger.ride.create

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abmodel.uwheels.databinding.FragmentPassengerCreateRideBinding

class CreateRideFragment : Fragment() {

	companion object {
		const val TAG = "CreateRideFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentPassengerCreateRideBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentPassengerCreateRideBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
