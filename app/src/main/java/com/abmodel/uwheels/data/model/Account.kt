package com.abmodel.uwheels.data.model

data class Account(
    val accountId : String,
    val email : String,
    val password : String,
    val name : String,
    val lastName : String,
    val contactNo : String,
    val Institution : String,
    val ridesCount: List<Long>,
    val avarage : Long,
    val isDriver : Boolean,
    val licenseId : String?,
    val vehicles : List<Vehicle>?,
    val rides : List<CreatedRide>
)
