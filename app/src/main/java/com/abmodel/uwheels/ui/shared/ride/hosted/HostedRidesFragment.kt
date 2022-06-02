package com.abmodel.uwheels.ui.shared.ride.hosted

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentHostedRidesBinding
import com.abmodel.uwheels.ui.shared.data.UserRidesFilter
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.ui.adapter.HostedRideItemAdapter
import com.abmodel.uwheels.ui.shared.data.SharedViewModelFactory
import com.abmodel.uwheels.ui.shared.ride.rides.RidesFragment

class HostedRidesFragment : Fragment() {

	companion object {
		const val TAG = "HostedRidesFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentHostedRidesBinding? = null
	private val binding get() = _binding!!

	private val sharedViewModel: SharedViewModel by activityViewModels {
		SharedViewModelFactory(requireActivity().application)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentHostedRidesBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			dataViewModel = sharedViewModel
			lifecycleOwner = viewLifecycleOwner

			// Set the adapter for the recycler view
			rides.adapter = HostedRideItemAdapter {
				Log.d(RidesFragment.TAG, "Clicked on ride $it")
				onRideSelected(it.id)
			}
			sharedViewModel.setUserRidesFilter(UserRidesFilter.HOSTED)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun onRideSelected(rideId: String) {
		sharedViewModel.selectUserRide(rideId)
		findNavController().navigate(
			R.id.action_hostedRidesFragment_to_hostedRideDetailFragment
		)
	}
}
