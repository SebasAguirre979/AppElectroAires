package com.tg.electroaires.model

data class GetVehiculo(
    val placa: String,
    val tipo: String
)


data class verificacionVehiculo(
    val placa: String
)
