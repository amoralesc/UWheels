package com.abmodel.uwheels.ui.passenger.contacts

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abmodel.uwheels.databinding.FragmentPassengerContactsBinding

class ContactsFragment : Fragment() {

	// Binding objects to access the view elements
	private var _binding: FragmentPassengerContactsBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentPassengerContactsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
