package com.abmodel.uwheels.ui.shared.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abmodel.uwheels.databinding.FragmentSearchAddressBinding

class SearchAddressFragment : Fragment() {

	companion object {
		const val TAG = "SearchAddressFragment"
		const val ARGUMENT_SELECTED_INPUT = "selected_input"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentSearchAddressBinding? = null
	private val binding get() = _binding!!

	// Arguments
	private lateinit var selectedInput: String

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// Get the selected input from the arguments
		selectedInput = arguments?.getString(ARGUMENT_SELECTED_INPUT).toString()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentSearchAddressBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Set the selected input
		when (selectedInput) {
			"source" -> {
				binding.searchSource.requestFocus()
			}
			"destination" -> {
				binding.searchDestination.requestFocus()
			}
			else -> {
				Log.w(TAG, "Invalid selected input")
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
