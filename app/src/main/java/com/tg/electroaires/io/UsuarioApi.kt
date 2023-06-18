package com.tg.electroaires.io
import com.tg.electroaires.model.DatosLogin
import com.tg.electroaires.model.Usuario
import com.tg.electroaires.model.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UsuarioApi {
    @GET("usuarios/")
    suspend fun obtenerUsuarios(): List<Usuario>

    @POST("usuarios/verificacion/")
    suspend fun verificarRegistro(@Body datosRegistro: DatosLogin): Response<UsuarioResponse>
}
