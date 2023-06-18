package com.tg.electroaires.model

data class Usuario(
    val id: Int,
    val correo: String,
    val contrasena: String,
    val nombre: String,
    val celular: String,
    val cedula: String,
    val rol: String
)
data class DatosLogin(
    val correo: String,
    val contrasena: String
)

data class UsuarioResponse(
    val nombre: String
)
