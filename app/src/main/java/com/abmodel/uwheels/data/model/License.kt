package com.abmodel.uwheels.data.model

import java.util.*

data class License(
    val expeditionDate : Date,
    val restrictions : List<String>,
    val licenseType : LicenseType,
    val expirationDate : Date
)

enum class LicenseType{
    A1,
    A2,
    B1,
    B2,
    B3
}