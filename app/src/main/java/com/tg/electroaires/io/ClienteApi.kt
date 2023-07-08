package com.tg.electroaires.io

import com.tg.electroaires.model.GetCliente
import com.tg.electroaires.model.verificacionCliente
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ClienteApi {
    @POST("clientes/verificacion/")
    fun verficacionCliente(@Header("Authorization") token: String, @Body verificacionCliente: verificacionCliente): Call<GetCliente>

    @POST("clientes/")
    fun crearCliente(@Header("Authorization") token: String, @Body getCliente: GetCliente): Call<Void>
}