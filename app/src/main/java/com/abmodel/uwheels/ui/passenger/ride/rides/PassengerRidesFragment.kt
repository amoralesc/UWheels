package com.abmodel.uwheels.ui.passenger.ride.rides

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.abmodel.uwheels.databinding.FragmentPassengerRidesBinding
import com.abmodel.uwheels.ui.passenger.ride.adapter.RideItemAdapter
import com.abmodel.uwheels.ui.shared.data.RidesPage
import com.abmodel.uwheels.ui.shared.data.SharedViewModel

class PassengerRidesFragment : Fragment() {

	companion object {
		const val TAG = "PassengerRidesFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentPassengerRidesBinding? = null
	private val binding get() = _binding!!

	private val sharedViewModel: SharedViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentPassengerRidesBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			dataViewModel = sharedViewModel
			lifecycleOwner = viewLifecycleOwner

			// Set the adapter for the recycler view
			rides.adapter = RideItemAdapter {
				Log.d(TAG, "Clicked on ride $it")
			}

			chipActive.setOnClickListener {
				sharedViewModel.setSelectedRidesPage(RidesPage.ACTIVE)
			}
			chipRequested.setOnClickListener {
				sharedViewModel.setSelectedRidesPage(RidesPage.REQUESTED)
			}
			chipCompleted.setOnClickListener {
				sharedViewModel.setSelectedRidesPage(RidesPage.COMPLETED)
			}
			sharedViewModel.setSelectedRidesPage(RidesPage.ACTIVE)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
