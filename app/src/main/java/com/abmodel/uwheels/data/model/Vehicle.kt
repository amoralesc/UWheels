package com.abmodel.uwheels.data.model

data class Vehicle(
    var make: String = "",
    var model: String = "",
    var year: Int = 0,
    var plate: String = "",
    var capacity: Int = 0,
    var color: Int = 0xFFFFFF,
    val current: Boolean = true
)
