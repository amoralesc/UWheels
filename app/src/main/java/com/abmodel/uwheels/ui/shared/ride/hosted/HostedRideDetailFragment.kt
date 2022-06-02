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
import com.abmodel.uwheels.databinding.FragmentHostedRideDetailBinding
import com.abmodel.uwheels.ui.adapter.HostedRideItemAdapter
import com.abmodel.uwheels.ui.adapter.RideRequestItemAdapter
import com.abmodel.uwheels.ui.adapter.RideUserItemAdapter
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.ui.shared.data.UserRidesFilter
import com.abmodel.uwheels.ui.shared.ride.rides.RidesFragment
import com.abmodel.uwheels.util.formatDateFromMillis
import com.abmodel.uwheels.util.formatTime

class HostedRideDetailFragment : Fragment() {

	companion object {
		const val TAG = "HostedRideDetailFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentHostedRideDetailBinding? = null
	private val binding get() = _binding!!

	private val sharedViewModel: SharedViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentHostedRideDetailBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			dataViewModel = sharedViewModel
			lifecycleOwner = viewLifecycleOwner

			startRide.setOnClickListener {
				onStartRidePressed()
			}
			cancelRide.setOnClickListener {
				onCancelRidePressed()
			}
			viewRequests.setOnClickListener {
				onViewRequestsPressed()
			}

			// Set the adapter for the recycler view
			passengers.adapter = RideUserItemAdapter {
				Log.d(TAG, "Passenger: ${it.name}")
			}

			// Load the hosted ride info
			sharedViewModel.selectedUserRide.observe(viewLifecycleOwner) {
				it?.let { ride ->
					wheelsType.text = ride.wheelsType
					source.text = ride.source.mainText
					destination.text = ride.destination.mainText
					capacity.text = getString(
						R.string.hosted_ride_capacity,
						ride.currentCapacity,
						ride.totalCapacity
					)
					requests.text = getString(
						R.string.hosted_ride_requests,
						ride.requests.size
					)

					ride.creationDate.apply {
						info.text = getString(
							R.string.hosted_ride_info,
							this.millis?.let { formatDateFromMillis(it) } ?: "",
							this.hour?.let { hour ->
								this.minute?.let { minute ->
									formatTime(hour, minute)
								} ?: ""
							} ?: ""
						)
					}
					ride.date.apply {
						date.text = getString(
							R.string.ride_date,
							this.millis?.let { formatDateFromMillis(it) } ?: "",
							this.hour?.let { hour ->
								this.minute?.let { minute ->
									formatTime(hour, minute)
								} ?: ""
							} ?: ""
						)
					}
				}
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun onStartRidePressed() {

	}

	private fun onCancelRidePressed() {

	}

	private fun onViewRequestsPressed() {
		findNavController().navigate(
			R.id.action_hostedRideDetailFragment_to_hostedRideRequestsFragment
		)
	}
}
