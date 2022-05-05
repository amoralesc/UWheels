package com.abmodel.uwheels.data.model

data class Vehicle (
    val nickname : String,
    val vehicleType : VehicleType,
    val registrationPlate : String,
    val model : String,
    val make : String,
    val variant : String,
    val status : Boolean
)

enum class VehicleType{
    CAR,
    MOTORBIKE
}

