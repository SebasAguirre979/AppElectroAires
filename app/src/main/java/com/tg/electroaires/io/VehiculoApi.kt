package com.tg.electroaires.io

import com.tg.electroaires.model.GetVehiculo
import com.tg.electroaires.model.verificacionVehiculo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface VehiculoApi {
    @POST("vehiculos-verificacion/")
    fun verficacionVehiculo(@Header("Authorization") token: String, @Body verificacionVehiculo: verificacionVehiculo): Call<GetVehiculo>

    @POST("vehiculos/")
    fun crearVehiculo(@Header("Authorization") token: String, @Body getVehiculo: GetVehiculo): Call<Void>
}