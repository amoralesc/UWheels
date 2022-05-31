package com.abmodel.uwheels.data.repository.driver.apply

import com.abmodel.uwheels.data.model.DriverApplication

/**
 * Driver Application repository interface that provides the data access layer
 * to sumbit the driver application form.
 */
interface DriverApplicationRepository {
	suspend fun submitDriverApplication(
		driverApplication: DriverApplication
	)
}