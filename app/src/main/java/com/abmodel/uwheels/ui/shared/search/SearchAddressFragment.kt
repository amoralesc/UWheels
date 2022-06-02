package com.abmodel.uwheels.ui.shared.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.abmodel.uwheels.data.model.CustomAddress
import com.abmodel.uwheels.databinding.FragmentSearchAddressBinding
import com.abmodel.uwheels.ui.adapter.AddressItemAdapter

class SearchAddressFragment : Fragment() {

	companion object {
		const val TAG = "SearchAddressFragment"
		const val ARGUMENT_SELECTED_INPUT = "selected_input"
		const val ARGUMENT_SOURCE_ADDRESS = "source"
		const val ARGUMENT_DESTINATION_ADDRESS = "destination"
		const val REQUEST_KEY = "search_results"
		const val RESULT_KEY_SOURCE = "source"
		const val RESULT_KEY_DESTINATION = "destination"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentSearchAddressBinding? = null
	private val binding get() = _binding!!

	private val viewModel: SearchAddressViewModel by viewModels()

	// Arguments
	private var selectedInput: String? = null
	private var sourceAddress: CustomAddress? = null
	private var destinationAddress: CustomAddress? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// Get the arguments
		selectedInput = arguments?.getString(ARGUMENT_SELECTED_INPUT).toString()
		sourceAddress = arguments?.getParcelable(ARGUMENT_SOURCE_ADDRESS)
		destinationAddress = arguments?.getParcelable(ARGUMENT_DESTINATION_ADDRESS)

		// Set the source and destination addresses on the view model
		sourceAddress?.let { viewModel.updateSourceAddress(it) }
		destinationAddress?.let { viewModel.updateDestinationAddress(it) }
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

		// Observe the view model
		// To update the selected search result
		viewModel.sourceAddress.observe(viewLifecycleOwner) { address ->
			binding.searchSource.setText(address.mainText)
			updateResults()
			// requestRideViewModel.updateSourceAddress(address)
		}
		viewModel.destinationAddress.observe(viewLifecycleOwner) { address ->
			binding.searchDestination.setText(address.mainText)
			updateResults()
			// requestRideViewModel.updateDestinationAddress(address)
		}

		// Set an after text changed listener to both inputs
		// This ensures that after the user types in the input, the results are updated
		// with places autocomplete suggestions
		val afterTextChangedListener = object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

			override fun afterTextChanged(s: Editable) {
				viewModel.autocompleteAddress(
					s.toString()
				)
			}
		}
		binding.searchSource.addTextChangedListener(afterTextChangedListener)
		binding.searchDestination.addTextChangedListener(afterTextChangedListener)

		binding.apply {
			lifecycleOwner = viewLifecycleOwner
			dataViewModel = viewModel

			// Set the on click listener for the selected address
			// in the recycler view
			searchResults.adapter = AddressItemAdapter { address ->
				Log.d(TAG, "Selected address: $address")
				selectedInput = if (binding.searchSource.hasFocus()) {
					"source"
				} else {
					"destination"
				}
				viewModel.selectAddress(address, selectedInput!!)
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun updateResults() {
		setFragmentResult(
			REQUEST_KEY,
			bundleOf(
				RESULT_KEY_SOURCE to viewModel.sourceAddress.value,
				RESULT_KEY_DESTINATION to viewModel.destinationAddress.value
			)
		)
	}
}
