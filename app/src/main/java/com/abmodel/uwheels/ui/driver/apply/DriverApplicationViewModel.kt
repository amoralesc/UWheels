package com.abmodel.uwheels.ui.driver.apply

import androidx.lifecycle.ViewModel

class DriverApplicationViewModel : ViewModel() {

	/*
	// The current page
	private val _currentPage = MutableLiveData(0)
	val currentPage: LiveData<Int>
		get() = _currentPage

	// Retrieves the data for the current page
	private val _currentPageData: MutableLiveData<DriverApplicationView>
		get() {
			val data = MutableLiveData<DriverApplicationView>()
			data.value = _pageData[currentPage.value!!]
			return data
		}
	val currentPageData: LiveData<DriverApplicationView>
		get() = _currentPageData

	fun nextPage() {
		if (currentPage.value!! < totalPages - 1) {
			_currentPage.value = currentPage.value!! + 1
		}
	}

	fun previousPage() {
		if (currentPage.value!! > 0) {
			_currentPage.value = currentPage.value!! - 1
		}
	}
	*/
}
