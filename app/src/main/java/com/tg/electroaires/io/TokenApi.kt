package com.tg.electroaires.io

import com.tg.electroaires.model.ResponseToken
import com.tg.electroaires.model.ResponseTokenRefresh
import com.tg.electroaires.model.Token
import com.tg.electroaires.model.TokenRefresh
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {
    @POST("api/token/")
    fun obtenerToken(
        @Body data: Token
    ): Call<ResponseToken>

    @POST("api/token/refresh/")
    fun renovarToken(
        @Body data: TokenRefresh
    ): Call<ResponseTokenRefresh>
}