package com.abmodel.uwheels.data.model

enum class VehicleType{
    CAR,
    MOTORBIKE
}

data class Vehicle (
    val nickname : String,
    val vehicleType : VehicleType,
    val registrationPlate : String,
    val model : String,
    val make : String,
    val variant : String,
    val status : Boolean
)
