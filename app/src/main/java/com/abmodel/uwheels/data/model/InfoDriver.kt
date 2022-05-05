package com.abmodel.uwheels.data.model

data class InfoDriver(
    val account: Account,
    val licenseId : String,
    val vehicles : List<Vehicle>,
    val rides : List<CreatedRide>
)
