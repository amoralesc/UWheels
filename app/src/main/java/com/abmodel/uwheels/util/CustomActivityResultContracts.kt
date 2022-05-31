package com.abmodel.uwheels.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper


/**
 * Custom [ActivityResultContract] for getting the success result and uri of the image
 * taken with the camera.
 *
 * Adapted from: Yanneck Reiß, Feb 11 2022, last accessed Apr 17 2022.
 * https://medium.com/codex/how-to-implement-the-activity-result-api-takepicture-contract-with-uri-return-type-7c93881f5b0f
 *
 * TODO. Known bug: if the fragment's view model is not a shared view model, the fragment
 * is probably destroyed, the uri is set to null in this activity result contract
 * and the app crashes when returning to the fragment.
 */
class TakePictureWithUriReturnContract : ActivityResultContract<Uri, Pair<Boolean, Uri>>() {

	private lateinit var imageUri: Uri

	@CallSuper
	override fun createIntent(context: Context, input: Uri): Intent {
		imageUri = input
		return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, input)
	}

	override fun getSynchronousResult(
		context: Context,
		input: Uri
	): SynchronousResult<Pair<Boolean, Uri>>? = null

	@Suppress("AutoBoxing")
	override fun parseResult(resultCode: Int, intent: Intent?): Pair<Boolean, Uri> {
		return (resultCode == Activity.RESULT_OK) to (imageUri)
	}
}
