package com.tg.electroaires.io

import com.tg.electroaires.model.PostValoraciones
import com.tg.electroaires.model.Valoraciones
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ValoracionApi {
    @POST("valoraciones/")
    fun postValoracion(@Body postValoraciones: PostValoraciones): Call<Void>

    @GET("valoraciones/")
    fun getValoraciones(): Call<List<Valoraciones>>
}