package com.abmodel.uwheels.ui.shared.ride.rides

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.abmodel.uwheels.databinding.FragmentRidesBinding
import com.abmodel.uwheels.ui.adapter.RideItemAdapter
import com.abmodel.uwheels.ui.shared.data.UserRidesFilter
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.ui.shared.data.SharedViewModelFactory

class RidesFragment : Fragment() {

	companion object {
		const val TAG = "RidesFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentRidesBinding? = null
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
		_binding = FragmentRidesBinding.inflate(inflater, container, false)
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
				sharedViewModel.setUserRidesFilter(UserRidesFilter.ACTIVE)
			}
			chipRequested.setOnClickListener {
				sharedViewModel.setUserRidesFilter(UserRidesFilter.REQUESTED)
			}
			chipCompleted.setOnClickListener {
				sharedViewModel.setUserRidesFilter(UserRidesFilter.COMPLETED)
			}
			sharedViewModel.setUserRidesFilter(UserRidesFilter.ACTIVE)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
