package com.abmodel.uwheels.ui.driver.apply

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.DriverApplication
import com.abmodel.uwheels.data.model.UploadedFile
import com.abmodel.uwheels.data.model.VehicleDetail
import com.abmodel.uwheels.data.repository.driver.apply.FirebaseDriverApplicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DriverApplicationViewModel : ViewModel() {

	companion object {
		private val driverApplicationForms = listOf(
			Pair("licenseFiles", R.string.license_files_name),
			Pair("ownershipFiles", R.string.ownership_files_name),
			Pair("insuranceFiles", R.string.insurance_files_name),
			Pair("vehiclePics", R.string.vehicle_pics_name),
			Pair("vehicleDetail", R.string.vehicle_detail_name),
		)
	}

	private val repository = FirebaseDriverApplicationRepository.getInstance()

	private val _driverApplication: DriverApplication = DriverApplication(
		licenseFiles = mutableListOf(),
		ownershipFiles = mutableListOf(),
		insuranceFiles = mutableListOf(),
		vehiclePics = mutableListOf(),
		vehicleDetail = VehicleDetail()
	)

	private var _currentFilesIndex = 0

	private val _currentFiles = MutableLiveData<MutableList<UploadedFile>>(
		mutableListOf()
	)
	val currentFiles: LiveData<MutableList<UploadedFile>>
		get() = _currentFiles

	fun addUploadedFile(file: UploadedFile) {
		val files = _currentFiles.value!!.toMutableList()
		files.add(file)
		_currentFiles.postValue(files)
	}

	fun removeUploadedFile(file: UploadedFile) {
		val files = _currentFiles.value!!.toMutableList()
		files.remove(file)
		_currentFiles.postValue(files)
	}

	fun getNextFileName(): Pair<Int, Int> {
		return Pair(
			driverApplicationForms[_currentFilesIndex].second,
			currentFiles.value!!.size
		)
	}

	fun setCurrentFilesIndex(index: Int) {
		// Save the current files in the driver application
		_driverApplication.apply {
			when (driverApplicationForms[_currentFilesIndex].first) {
				"licenseFiles" -> {
					licenseFiles.clear()
					licenseFiles.addAll(_currentFiles.value!!)
				}
				"ownershipFiles" -> {
					ownershipFiles.clear()
					ownershipFiles.addAll(_currentFiles.value!!)
				}
				"insuranceFiles" -> {
					insuranceFiles.clear()
					insuranceFiles.addAll(_currentFiles.value!!)
				}
				"vehiclePics" -> {
					vehiclePics.clear()
					vehiclePics.addAll(_currentFiles.value!!)
				}
			}
		}

		_currentFilesIndex = index
		assert(_currentFilesIndex in 0..driverApplicationForms.lastIndex)

		when (driverApplicationForms[_currentFilesIndex].first) {
			"licenseFiles" -> _currentFiles.value =
				_driverApplication.licenseFiles.toMutableList()
			"ownershipFiles" -> _currentFiles.value =
				_driverApplication.ownershipFiles.toMutableList()
			"insuranceFiles" -> _currentFiles.value =
				_driverApplication.insuranceFiles.toMutableList()
			"vehiclePics" -> _currentFiles.value =
				_driverApplication.vehiclePics.toMutableList()
			"vehicleDetail" -> _currentFiles.value =
				mutableListOf()
		}
	}

	suspend fun submitApplication() {
		repository.submitDriverApplication(_driverApplication)
	}
}
