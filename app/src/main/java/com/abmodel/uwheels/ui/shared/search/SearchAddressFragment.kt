package com.abmodel.uwheels.ui.shared.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abmodel.uwheels.databinding.FragmentSearchAddressBinding
import com.abmodel.uwheels.ui.shared.search.adapter.AddressItemAdapter

class SearchAddressFragment : Fragment() {

	companion object {
		const val TAG = "SearchAddressFragment"
		const val ARGUMENT_SELECTED_INPUT = "selected_input"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentSearchAddressBinding? = null
	private val binding get() = _binding!!

	private val searchAddressViewModel: SearchAddressViewModel by viewModels {
		SearchAddressViewModelFactory(requireActivity().application)
	}

	// Arguments
	private var selectedInput: String? = null

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
				binding.searchSource.requestFocusFromTouch()
			}
			"destination" -> {
				binding.searchDestination.requestFocusFromTouch()
			}
			else -> {
				Log.wtf(TAG, "Invalid selected input")
				selectedInput = "source"
				binding.searchSource.requestFocusFromTouch()
			}
		}

		val afterTextChangedListener = object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

			override fun afterTextChanged(s: Editable) {
				searchAddressViewModel.autocompleteAddress(
					s.toString()
				)
			}
		}
		binding.searchSource.addTextChangedListener(afterTextChangedListener)
		binding.searchDestination.addTextChangedListener(afterTextChangedListener)

		// Set the on click listener for the selected address
		// in the recycler view
		binding.apply {
			lifecycleOwner = viewLifecycleOwner
			viewModel = searchAddressViewModel

			searchResults.adapter = AddressItemAdapter { address ->
				Log.d(TAG, "Selected address: $address")
				selectedInput = if (binding.searchSource.hasFocus()) {
					"source"
				} else {
					"destination"
				}
				searchAddressViewModel.selectAddress(address, selectedInput!!)
			}
		}

		searchAddressViewModel.sourceAddress.observe(viewLifecycleOwner) { address ->
			binding.searchSource.setText(address.mainText)
		}
		searchAddressViewModel.destinationAddress.observe(viewLifecycleOwner) { address ->
			binding.searchDestination.setText(address.mainText)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
