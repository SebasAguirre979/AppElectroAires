package com.tg.electroaires.model

data class Repuesto(
    val id: Int,
    val r_nombre_repuesto: String,
    val r_cantidad: Int,
    val r_valor_proveedor: Double,
    val r_valor_publico: Double
)
