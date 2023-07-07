package com.tg.electroaires.io

import com.tg.electroaires.model.DatosLogin
import com.tg.electroaires.model.Servicio
import com.tg.electroaires.model.ServicioCompleto
import com.tg.electroaires.model.UpdateRepuesto
import com.tg.electroaires.model.UsuarioResponse
import com.tg.electroaires.model.Valoraciones
import com.tg.electroaires.model.createRepuesto
import com.tg.electroaires.model.putServicio
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ServicioApi {
    @GET("servicios/")
    suspend fun obtenerServicios(): List<Servicio>

    @GET("servicioclientevehiculo/{id}")
    suspend fun getServiceById(@Path("id") id: Int): Response<ServicioCompleto>

    @DELETE("deleterepuesto/{id}/")
    fun deleteRepuestoById(@Path("id") id: Int): Call<Void>

    @PUT("updaterepuesto/{id}/")
    fun updateRepuestoById(@Path("id") id: Int, @Body data: UpdateRepuesto): Call<Void>

    @POST("createrepuesto/{id}/")
    fun addRpuestoServicio(@Path("id") id: Int, @Body createRepuesto: createRepuesto): Call<Void>

    @PUT("servicios/{id}/")
    fun putServicio(@Path("id") id: Int, @Body data: putServicio): Call<Void>

    @GET("serviciosplaca/{vehiculo_id}/")
    fun getServicioPlaca(@Path("vehiculo_id") vehiculo_id: String): Call<List<ServicioCompleto>>
}