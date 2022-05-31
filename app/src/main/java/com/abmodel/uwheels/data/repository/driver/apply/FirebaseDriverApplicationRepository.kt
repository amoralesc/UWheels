package com.abmodel.uwheels.data.repository.driver.apply

import com.abmodel.uwheels.data.model.DriverApplication
import com.abmodel.uwheels.data.model.UploadedFile
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository

class FirebaseDriverApplicationRepository internal constructor(
	private val dataSource: FirebaseDriverApplicationDataSource
) : DriverApplicationRepository {

	/**
	 * The instance is created when the application starts and stays alive
	 * throughout the application lifecycle.
	 */
	companion object {
		@Volatile
		private var instance: FirebaseDriverApplicationRepository? = null

		fun getInstance(): FirebaseDriverApplicationRepository {
			return instance ?: synchronized(this) {
				instance ?: FirebaseDriverApplicationRepository(
					FirebaseDriverApplicationDataSource.getInstance()
				).also { instance = it }
			}
		}

		const val TAG = "FirebaseDriverApplicationRepository"
	}

	override suspend fun submitDriverApplication(driverApplication: DriverApplication) {
		val userId =
			FirebaseAuthRepository.getInstance().getLoggedInUser().uid

		driverApplication.licenseFiles = uploadDriverApplicationFiles(
			userId, "licenseFiles", driverApplication.licenseFiles
		)
		driverApplication.ownershipFiles = uploadDriverApplicationFiles(
			userId, "ownershipFiles", driverApplication.ownershipFiles
		)
		driverApplication.insuranceFiles = uploadDriverApplicationFiles(
			userId, "insuranceFiles", driverApplication.insuranceFiles
		)
		driverApplication.vehiclePics = uploadDriverApplicationFiles(
			userId, "vehiclePics", driverApplication.vehiclePics
		)

		dataSource.uploadDriverApplication(userId, driverApplication)
		FirebaseAuthRepository.getInstance().makeUserDriver()
		FirebaseAuthRepository.getInstance().setDriverMode(true)
	}

	private suspend fun uploadDriverApplicationFiles(
		userId: String,
		filesType: String,
		files: MutableList<UploadedFile>,
	): MutableList<UploadedFile> {
		val uploadedUris =
			dataSource.uploadDriverApplicationFiles(userId, filesType, files)

		for (i in 0 until files.size) {
			files[i].uri = uploadedUris[i]
		}
		return files
	}
}
