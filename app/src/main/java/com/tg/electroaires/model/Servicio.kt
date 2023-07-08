package com.tg.electroaires.model

data class Servicio(
    val id: Int,
    val cliente: String,
    val s_descripcion: String,
    val s_mano_obra: String,
    val s_fecha_entrada: String,
    val s_fecha_salida: String,
    val s_total: Double,
    val estado: Boolean,
    val s_vehiculo: String
)

data class ServicioCompleto(
    val id: Int,
    val cliente: Cliente,
    val s_descripcion: String,
    val s_vehiculo: Vehiculo,
    val s_mano_obra: Double,
    val s_fecha_entrada: String,
    val s_fecha_salida: String,
    val s_total: Double,
    val estado: Boolean,
    val detalles_servicio: List<DetalleServicio>,
)

data class Cliente(
    val cedula: Long,
    val nombre: String,
    val celular: Long
)

data class Vehiculo(
    val placa: String,
    val tipo: String
)

data class DetalleServicio(
    val id: Int,
    val r_nombre_repuesto: String,
    val r_valor_publico: Double,
    val s_cantidad: Int
)

data class UpdateRepuesto(
    val s_cantidad: Int
    )

data class createRepuesto(
    val repuesto: Int,
    val s_cantidad: Int
)

data class putServicio(
    val s_descripcion: String,
    val s_mano_obra: Double,
    val estado: Boolean,
    val cliente: String,
    val s_vehiculo: String,
)

data class postServicio(
    val s_descripcion: String,
    val s_mano_obra: Double,
    val cliente: Long,
    val s_vehiculo: String,
)
