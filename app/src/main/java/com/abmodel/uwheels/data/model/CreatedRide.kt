package com.abmodel.uwheels.data.model

import java.util.*

/**
 * CreatedRide: data class that captures trip information.
 */
data class CreatedRide(
    val rideId : String,
    val driverId : String,
    val passengers : List<Account>,
    val from : InfoRide,
    val to: InfoRide,
    val leaveTime : Date,
    val comments : List<String>,
    val carPlate: String,
    val serviceType : String,
    val vehicle : Vehicle,
    val value : Double,
    val payments : List<Payment>
)

