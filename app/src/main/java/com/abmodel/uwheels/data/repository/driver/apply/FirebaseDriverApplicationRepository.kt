package com.abmodel.uwheels.data.repository.driver.apply

import android.util.Log
import com.abmodel.uwheels.data.FirestorePaths
import com.abmodel.uwheels.data.StoragePaths
import com.abmodel.uwheels.data.model.DriverApplication
import com.abmodel.uwheels.data.model.UploadedFile
import com.abmodel.uwheels.data.model.Vehicle
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class FirebaseDriverApplicationRepository internal constructor(
	private val mFirestore: FirebaseFirestore,
	private val mStorage: FirebaseStorage
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
					Firebase.firestore,
					Firebase.storage
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

		uploadDriverApplication(userId, driverApplication)
		FirebaseAuthRepository.getInstance().makeUserDriver()
		FirebaseAuthRepository.getInstance().setDriverMode(true)
	}

	private suspend fun uploadDriverApplicationFiles(
		userId: String,
		filesType: String,
		files: MutableList<UploadedFile>,
	): MutableList<UploadedFile> {

		files.forEach { file ->
			val uri = mStorage.reference
				.child(StoragePaths.USERS)
				.child(userId)
				.child(StoragePaths.DRIVER_APPLICATION)
				.child(filesType)
				.child(file.name)
				.putFile(file.uri)
				.await()
				.storage
				.downloadUrl
				.await()

			// Update the file's uri
			file.uri = uri
		}

		return files
	}

	private suspend fun uploadDriverApplication(
		userId: String,
		driverApplication: DriverApplication
	) {
		val vehicle =
			Vehicle(
				make = driverApplication.vehicleDetail?.make ?: "",
				model = driverApplication.vehicleDetail?.model ?: "",
				year = driverApplication.vehicleDetail?.year ?: 0,
				plate = driverApplication.vehicleDetail?.plate ?: "",
				capacity = driverApplication.vehicleDetail?.capacity ?: 0,
				color = driverApplication.vehicleDetail?.color ?: 0xFFFFFF,
			)

		mFirestore
			.collection(FirestorePaths.USERS)
			.document(userId)
			.update(
				FirestorePaths.DRIVER_APPLICATION, driverApplication,
				"vehicles", listOf(vehicle)
			)
			.addOnSuccessListener {
				Log.d(
					TAG, "Driver application uploaded successfully"
				)
			}
			.addOnFailureListener {
				Log.d(TAG, "Driver application upload failed")
			}
			.await()
	}
}
