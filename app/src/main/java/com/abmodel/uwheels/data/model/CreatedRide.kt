package com.abmodel.uwheels.data.model

import java.util.*

/**
 * CreatedRide: data class that captures trip information.
 */
data class CreatedRide(
    val rideId : String,
    val driverId : String,
    val passengers : List<InfoPassenger>,
    val from : InfoRide,
    val to: InfoRide,
    val leave_time : Date
)

/**
 * ServiceDriver: data class that captures service driver information, one of the attributes of this class
 * contains trip information "CreatedRide"
 */
data class ServiceDriver (
    val ride: CreatedRide,
    val driverId : String,
    val vehicle : Vehicle,
    val value : Double,
    val payments : List<Payment>
)

/**
 * ServiceClassic: data class that captures service classic information, the attribute of this class
 * contains service driver information "ServiceDriver"
 */
data class ServiceClassic(
    val serviceDriver : ServiceDriver
)

/**
* ServiceTwo: data class that captures service two information, one of the attributes of this class
* contains service driver information "ServiceDriver"
*/
data class ServiceTwo(
    val serviceDriver : ServiceDriver,
    val needHelmet : Boolean
)

/**
 * ServiceShared: data class that captures service shared information, one of the attributes of this class
 * contains trip information "CreatedRide"
 */
data class ServiceShared(
    val ride: CreatedRide,
    val author : InfoPassenger,
    val sharedUsed : RideSharing
)


enum class RideSharing{
    TAXI,
    UBER,
    BEAT,
    PUBLIC_TRANSPORTATION
}