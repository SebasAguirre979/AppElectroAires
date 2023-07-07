package com.tg.electroaires.io
import com.tg.electroaires.model.CambioContrasena
import com.tg.electroaires.model.DatosLogin
import com.tg.electroaires.model.PostValoraciones
import com.tg.electroaires.model.Usuario
import com.tg.electroaires.model.UsuarioResponse
import com.tg.electroaires.model.putServicio
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuarioApi {
    @GET("usuarios/")
    suspend fun obtenerUsuarios(): List<Usuario>

    @POST("usuarios/verificacion/")
    suspend fun verificarRegistro(@Body datosRegistro: DatosLogin): Response<UsuarioResponse>

    @POST("usuarios/cambiocontrasena/{id}/")
    fun cambioContrasena(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body data: CambioContrasena
    ): Call<Void>
}
