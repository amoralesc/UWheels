package com.abmodel.uwheels.ui.driver.apply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentDriverApplicationBinding

const val totalPages = 5

class DriverApplicationFragment : Fragment() {

	// The data used to display the page
	private lateinit var titles: Array<String>
	private lateinit var descriptions: Array<String>
	private lateinit var supportedFiles: Array<String>

	private lateinit var _pageData: List<DriverApplicationView>

	// The current page number
	private var currentPage = 0

	// Binding objects to access the view elements
	private var _binding: FragmentDriverApplicationBinding? = null
	private val binding get() = _binding!!

	// private val viewModel: DriverApplicationViewModel by viewModels()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentDriverApplicationBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Initialize the data
		initializeData()
		updateUI()

		binding.apply {
			back.setOnClickListener {
				onBackPressed()
			}
			next.setOnClickListener {
				onNextPressed()
			}
		}
	}

	private fun initializeData() {
		titles = requireContext().resources
			.getStringArray(R.array.driver_application_title)
		descriptions = requireContext().resources
			.getStringArray(R.array.driver_application_description)
		supportedFiles = requireContext().resources
			.getStringArray(R.array.driver_application_files)
		_pageData = listOf(
			DriverApplicationView(
				titles[0],
				descriptions[0],
				supportedFiles[0],
				false
			),
			DriverApplicationView(
				titles[1],
				descriptions[1],
				supportedFiles[1],
				false
			),
			DriverApplicationView(
				titles[2],
				descriptions[2],
				supportedFiles[2],
				false
			),
			DriverApplicationView(
				titles[3],
				descriptions[3],
				supportedFiles[3],
				false
			),
			DriverApplicationView(
				titles[4],
				descriptions[4],
				supportedFiles[4],
				true
			)
		)
	}

	private fun onBackPressed() {

		if (currentPage > 0) {
			currentPage--
			updateUI()
		} else {
			findNavController().navigateUp()
		}
	}

	private fun onNextPressed() {

		if (currentPage < totalPages - 1) {
			currentPage++
			updateUI()
		} else {
			findNavController().navigate(
				R.id.action_driverApplicationFragment_to_passengerHomeFragment
			)
		}
	}

	private fun updateUI() {

		binding.apply {

			textTitle.text = _pageData[currentPage].title
			textDescription.text = _pageData[currentPage].description
			textSupportedFiles.text = _pageData[currentPage].supportedFiles

			if (_pageData[currentPage].swapToVehicleDetail) {
				layoutUploadFiles.visibility = View.GONE
				layoutVehicleDetails.visibility = View.VISIBLE
			} else {
				layoutVehicleDetails.visibility = View.GONE
				layoutUploadFiles.visibility = View.VISIBLE
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
