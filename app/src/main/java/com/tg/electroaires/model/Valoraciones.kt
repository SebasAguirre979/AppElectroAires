package com.tg.electroaires.model

data class PostValoraciones(
    val calificacion: Int,
    val opinion: String,
    val servicio: Int
)

data class Valoraciones(
    val id: Int,
    val calificacion: Int,
    val opinion: String,
    val fecha: String,
    val servicio: Int
)
