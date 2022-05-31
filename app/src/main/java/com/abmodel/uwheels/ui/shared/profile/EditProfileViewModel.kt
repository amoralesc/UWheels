package com.abmodel.uwheels.ui.shared.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.UploadedFile
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.ui.shared.data.FormResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

	private val repository = FirebaseAuthRepository.getInstance()

	private val _saveResult = MutableLiveData<FormResult>()
	val saveResult: LiveData<FormResult> = _saveResult

	init {
		_saveResult.value = FormResult()
	}

	fun editProfile(name: String, lastName: String, phone: String, photoFile: UploadedFile?) {

		viewModelScope.launch(Dispatchers.Main) {
			if (checkEditProfileForm(name, lastName, phone)) {

				val user = repository.getLoggedInUser()

				val newName = if (user.name != name) name else null
				val newLastName = if (user.lastName != lastName) lastName else null
				val newPhone = if (user.phone != phone) phone else null

				repository.updateUser(
					newName,
					newLastName,
					newPhone,
					photoFile
				)
				repository.fetchLoggedInUser()

				_saveResult.postValue(FormResult(success = true))
			}
		}
	}

	private fun checkEditProfileForm(
		name: String, lastName: String, phone: String
	): Boolean {

		return if (name.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
			_saveResult.value = FormResult(error = R.string.empty_fields)
			false
		} else {
			true
		}
	}
}