package com.abmodel.uwheels.ui.driver.apply

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.BuildConfig
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.UploadedFile
import com.abmodel.uwheels.databinding.FragmentDriverApplicationBinding
import com.abmodel.uwheels.ui.driver.apply.adapter.UploadedFileItemAdapter
import com.abmodel.uwheels.ui.driver.apply.data.DriverApplicationView
import com.abmodel.uwheels.util.TEMP_IMG_FILE_EXT
import com.abmodel.uwheels.util.TEMP_IMG_FILE_NAME
import com.abmodel.uwheels.util.TakePictureWithUriReturnContract
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class DriverApplicationFragment : Fragment() {

	companion object {
		const val TAG = "DriverApplicationFragment"
		private const val totalPages = 5

		private val IMAGE_MIME_TYPES = arrayOf("image/*")
		private val DOCUMENT_MIME_TYPE = arrayOf(
			"image/*",
			"application/pdf",
			"application/msword",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
		)
		private val ALL_MIME_TYPE = IMAGE_MIME_TYPES + DOCUMENT_MIME_TYPE
	}

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

	private var selectedColor: Int = 0xFFFFFFFF.toInt()

	private val viewModel: DriverApplicationViewModel by viewModels()

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
			dataViewModel = viewModel
			lifecycleOwner = viewLifecycleOwner

			files.adapter = UploadedFileItemAdapter(
				onItemClicked = { file ->
					Log.d(TAG, "Selected file for viewing: $file")
					viewFile(file)
				},
				onRemoveItemClicked = { file ->
					Log.d(TAG, "Selected file for deletion: $file")
					viewModel.removeUploadedFile(file)
				}
			)

			// Set the click listener
			// Upload file operations
			upload.setOnClickListener {
				onUploadPressed()
			}
			camera.setOnClickListener {
				onCameraPressed()
			}

			// Pick color
			pickColor.setOnClickListener {
				onPickColorPressed()
			}
			color.setOnClickListener {
				onPickColorPressed()
			}

			// (Navigation between pages)
			back.setOnClickListener {
				onBackPressed()
			}
			next.setOnClickListener {
				onNextPressed()
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun initializeData() {
		titles = requireContext().resources
			.getStringArray(R.array.driver_application_title)
		descriptions = requireContext().resources
			.getStringArray(R.array.driver_application_description)
		supportedFiles = requireContext().resources
			.getStringArray(R.array.driver_application_files)
		_pageData = listOf(
			DriverApplicationView( // License
				titles[0],
				descriptions[0],
				supportedFiles[0],
				ALL_MIME_TYPE,
				false
			),
			DriverApplicationView(  // Proof of ownership
				titles[1],
				descriptions[1],
				supportedFiles[1],
				ALL_MIME_TYPE,
				false
			),
			DriverApplicationView(  // Insurance
				titles[2],
				descriptions[2],
				supportedFiles[2],
				ALL_MIME_TYPE,
				false
			),
			DriverApplicationView(  // Pictures
				titles[3],
				descriptions[3],
				supportedFiles[3],
				IMAGE_MIME_TYPES,
				false
			),
			DriverApplicationView(  // Details
				titles[4],
				descriptions[4],
				supportedFiles[4],
				arrayOf(),
				true
			)
		)
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

	private fun onPickColorPressed() {
		ColorPickerDialogBuilder
			.with(context)
			.initialColor(0xFFFFFFFF.toInt())
			.wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
			.density(10)
			.setOnColorSelectedListener { color ->
				selectedColor = color
			}
			.setPositiveButton(
				getString(R.string.ok)
			) { _, _, _ ->
				binding.color.drawable.setTint(selectedColor)
				Log.d(TAG, "Selected color: $selectedColor")
			}
			.setNegativeButton(
				getString(R.string.cancel)
			) { _, _ -> }
			.build()
			.show()
	}

	private fun onBackPressed() {

		if (currentPage > 0) {
			currentPage--
			viewModel.setCurrentFilesIndex(currentPage)
			updateUI()
		} else {
			findNavController().navigateUp()
		}
	}

	private fun onNextPressed() {

		if (currentPage < totalPages - 1) {
			currentPage++
			viewModel.setCurrentFilesIndex(currentPage)
			updateUI()
		} else { // Finished the application
			onApplicationFinished()
		}
	}

	private fun onApplicationFinished() {
		binding.apply {
			viewModel.setVehicleDetail(
				vehicleMake.text.toString(),
				vehicleModel.text.toString(),
				vehicleYear.text.toString().toInt(),
				vehiclePlate.text.toString(),
				vehicleCapacity.text.toString().toInt(),
				selectedColor
			)
		}
		CoroutineScope(Dispatchers.Main).launch {
			viewModel.submitApplication()
		}
		goToHomeScreen()
	}

	private fun goToHomeScreen() {
		// Draw a welcome message
		Toast.makeText(
			requireContext(),
			getString(R.string.welcome_driver),
			Toast.LENGTH_LONG
		).show()

		findNavController().navigate(
			R.id.action_driverApplicationFragment_to_driverHomeFragment
		)
	}

	/**
	 * Calls [getFileFromDeviceResult] with the "image" MIME type
	 */
	private fun onUploadPressed() {
		getFileFromDeviceResult.launch(
			_pageData[currentPage].mimeTypes
		)
	}

	/**
	 * Calls [takePictureResult] only if the scope is STARTED.
	 * Passes the temporal uri generated by [getTmpFileUri]
	 */
	private fun onCameraPressed() {
		lifecycleScope.launchWhenStarted {
			getTmpFileUri().let { uri ->
				Log.d(TAG, "Generated temp uri: $uri")
				takePictureResult.launch(uri)
			}
		}
	}

	/**
	 * Launches an implicit intent to get a file from the device
	 */
	private val getFileFromDeviceResult =
		registerForActivityResult(
			ActivityResultContracts.OpenDocument()
		) { uri: Uri? ->
			if (uri != null) {
				onFileUploaded(uri)
			}
		}

	/**
	 * Launches an implicit intent to take a picture with the device's camera app
	 */
	private val takePictureResult =
		registerForActivityResult(
			TakePictureWithUriReturnContract()
		) { (isSuccess, imageUri) ->
			if (isSuccess) {
				onFileUploaded(imageUri)
			}
		}

	private fun onFileUploaded(uri: Uri) {
		// Get the file mimetype
		val fileMimeType = requireContext().contentResolver
			.getType(uri)
		// Get the file extension from the mimetype
		val fileExtension = fileMimeType?.substring(
			fileMimeType.lastIndexOf("/") + 1
		)
		// Get the file name
		val fileName = viewModel.getNextFileName().let {
			requireContext().getString(it.first, it.second, fileExtension)
		}

		viewModel.addUploadedFile(
			UploadedFile(
				fileName,
				fileMimeType ?: "",
				uri
			)
		)
	}

	/**
	 * Generates a temporal file uri to be used to store the picture taken
	 */
	private fun getTmpFileUri(): Uri {
		val tmpFile = File.createTempFile(
			TEMP_IMG_FILE_NAME, TEMP_IMG_FILE_EXT, activity?.cacheDir
		).apply {
			createNewFile()
			deleteOnExit()
		}

		return FileProvider.getUriForFile(
			requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile
		)
	}

	private fun viewFile(file: UploadedFile) {
		val intent = Intent(Intent.ACTION_VIEW).apply {
			setDataAndType(file.uri, file.mimeType)
			addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		}
		startActivity(intent)
	}
}
