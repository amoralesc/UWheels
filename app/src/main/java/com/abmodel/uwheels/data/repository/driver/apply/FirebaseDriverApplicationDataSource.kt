package com.abmodel.uwheels.data.repository.driver.apply

import android.net.Uri
import android.util.Log
import com.abmodel.uwheels.data.FirestorePaths
import com.abmodel.uwheels.data.StoragePaths
import com.abmodel.uwheels.data.model.DriverApplication
import com.abmodel.uwheels.data.model.UploadedFile
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class FirebaseDriverApplicationDataSource {

	companion object {
		@Volatile
		private var instance: FirebaseDriverApplicationDataSource? = null

		fun getInstance() = instance ?: synchronized(this) {
			instance ?: FirebaseDriverApplicationDataSource().also { instance = it }
		}

		const val TAG = "FirebaseDriverApplicationDataSource"
	}

	private val mFirestore = Firebase.firestore
	private val mStorage = Firebase.storage

	/**
	 * Uploads the files to Firebase Storage and returns the urls of the uploaded files
	 */
	suspend fun uploadDriverApplicationFiles(
		userId: String,
		filesType: String,
		files: MutableList<UploadedFile>
	): MutableList<Uri> {

		val uris = mutableListOf<Uri>()

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

			uris.add(uri)
		}

		return uris
	}

	suspend fun uploadDriverApplication(userId: String, driverApplication: DriverApplication) {

		mFirestore
			.collection(FirestorePaths.USERS)
			.document(userId)
			.update(
				FirestorePaths.DRIVER_APPLICATION,
				driverApplication
			)
			.addOnSuccessListener {
				Log.d(TAG, "Driver application uploaded successfully")
			}
			.addOnFailureListener {
				Log.d(TAG, "Driver application upload failed")
			}
			.await()
	}
}
