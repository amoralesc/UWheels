package com.abmodel.uwheels.ui.driver.ride.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.abmodel.uwheels.R
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.ui.shared.data.SharedViewModelFactory

class DriverCreateRideFragment : Fragment() {

	private val sharedViewModel: SharedViewModel by activityViewModels {
		SharedViewModelFactory(requireActivity().application)
	}
}
