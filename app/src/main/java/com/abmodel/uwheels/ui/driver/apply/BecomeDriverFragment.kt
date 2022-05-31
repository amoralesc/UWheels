package com.abmodel.uwheels.ui.driver.apply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentBecomeDriverBinding

class BecomeDriverFragment : Fragment() {

	// Binding objects to access the view elements
	private var _binding: FragmentBecomeDriverBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentBecomeDriverBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			becomeDriver.setOnClickListener {
				goToDriverApplicationScreen()
			}
			continuePassenger.setOnClickListener {
				goToHomeScreen()
			}
		}
	}

	private fun goToDriverApplicationScreen() {
		findNavController().navigate(
			R.id.action_becomeDriverFragment_to_driverApplicationFragment
		)
	}

	private fun goToHomeScreen() {
		findNavController().navigate(
			R.id.action_becomeDriverFragment_to_passengerHomeFragment
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
