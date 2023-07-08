package com.tg.electroaires.model

data class GetCliente(
    val cedula: Long,
    val nombre: String,
    val celular: Long
)

data class verificacionCliente(
    val cedula: Long
)
