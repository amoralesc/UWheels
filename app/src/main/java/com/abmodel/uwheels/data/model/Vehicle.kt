package com.abmodel.uwheels.data.model

data class Vehicle(
    var make: String,
    var model: String,
    var year: Int,
    var plate: String,
    var capacity: Int,
    var color: Int,
    val current: Boolean = true
)
