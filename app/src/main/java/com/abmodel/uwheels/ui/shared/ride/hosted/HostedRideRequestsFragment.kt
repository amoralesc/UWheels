package com.abmodel.uwheels.ui.shared.ride.hosted

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentHostedRideRequestsBinding
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.ui.adapter.RideRequestItemAdapter
import com.abmodel.uwheels.util.formatDateFromMillis
import com.abmodel.uwheels.util.formatTime

class HostedRideRequestsFragment : Fragment() {

	companion object {
		const val TAG = "HostedRideRequestsFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentHostedRideRequestsBinding? = null
	private val binding get() = _binding!!

	private val sharedViewModel: SharedViewModel by activityViewModels()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentHostedRideRequestsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			dataViewModel = sharedViewModel
			lifecycleOwner = viewLifecycleOwner

			// Set the adapter for the recycler view
			requests.adapter = RideRequestItemAdapter(
				onAcceptClicked = { request ->
					Log.d(TAG, "Accepted request: $request")
					sharedViewModel.acceptRideRequest(request)
				},
				onRejectClicked = { request ->
					Log.d(TAG, "Rejected request: $request")
					sharedViewModel.rejectRideRequest(request)
				}
			)

			// Load the hosted ride info
			sharedViewModel.selectedUserRide.observe(viewLifecycleOwner) {
				it?.let { ride ->
					wheelsType.text = ride.wheelsType
					source.text = ride.source.mainText
					destination.text = ride.destination.mainText
					capacity.text = capacity.context.getString(
						R.string.hosted_ride_capacity,
						ride.currentCapacity,
						ride.totalCapacity
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
}
