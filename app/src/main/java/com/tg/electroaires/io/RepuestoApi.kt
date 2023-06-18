package com.tg.electroaires.io

import retrofit2.Call
import com.tg.electroaires.model.Repuesto
import retrofit2.http.GET
import retrofit2.http.Path

interface RepuestoApi {
    @GET("repuestos/")
    fun getRepuestos(): Call<List<Repuesto>>
}