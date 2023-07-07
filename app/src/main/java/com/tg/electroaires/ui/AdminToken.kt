package com.tg.electroaires.ui

import android.util.Log
import com.tg.electroaires.io.RetrofitClient
import com.tg.electroaires.model.ResponseToken
import com.tg.electroaires.model.ResponseTokenRefresh
import com.tg.electroaires.model.Token
import com.tg.electroaires.model.TokenRefresh
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AdminToken {
    private var accessToken: String? = null
    private var refreshToken: String? = null

    // Método para generar o renovar el token
    fun generateOrRefreshToken(onTokenReady: () -> Unit) {
        val tokenService = RetrofitClient.tokenApi

        val username = "admin"
        val password = "admin"

        if (accessToken.isNullOrEmpty()) {
            // Datos para la generación del token
            val tokenData = Token(username, password/* datos necesarios para la generación del token */)

            // Realizar la llamada para obtener el token de acceso
            val obtenerTokenCall = tokenService.obtenerToken(tokenData)
            obtenerTokenCall.enqueue(object : Callback<ResponseToken> {
                override fun onResponse(call: Call<ResponseToken>, response: Response<ResponseToken>) {
                    if (response.isSuccessful) {
                        val tokenResponse = response.body()
                        if (tokenResponse != null) {
                            accessToken = tokenResponse.access
                            refreshToken = tokenResponse.refresh
                            onTokenReady.invoke()
                            Log.d("Correctamente Pedido","${response.message()}")
                        } else {
                            // Manejar la respuesta de error
                            Log.d("Error1","${response.message()}")
                        }
                    } else {
                        // Manejar la respuesta de error
                        Log.d("Error2","${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseToken>, t: Throwable) {
                    // Manejar el fallo en la comunicación
                    Log.d("Error3","${t.message}")
                }
            })
        } else {
            // Datos para la renovación del token
            val tokenRefreshData = TokenRefresh(refreshToken.toString())

            // Realizar la llamada para renovar el token
            val renovarTokenCall = tokenService.renovarToken(tokenRefreshData)
            renovarTokenCall.enqueue(object : Callback<ResponseTokenRefresh> {
                override fun onResponse(call: Call<ResponseTokenRefresh>, response: Response<ResponseTokenRefresh>) {
                    if (response.isSuccessful) {
                        val tokenResponse = response.body()
                        if (tokenResponse != null) {
                            accessToken = tokenResponse.access
                            onTokenReady.invoke()
                            Log.d("Correctamente Renovado","${response.message()}")
                        } else {
                            // Manejar la respuesta de error
                            Log.d("Error4","${response.message()}")
                        }
                    } else {
                        // Manejar la respuesta de error
                        Log.d("Error5","${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseTokenRefresh>, t: Throwable) {
                    // Manejar el fallo en la comunicación
                    Log.d("Error6","${t.message}")
                }
            })
        }
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun setAccessToken(token: String) {
        accessToken = token
    }

    fun getRefreshToken(): String? {
        return refreshToken
    }

    fun setRefreshToken(token: String) {
        refreshToken = token
    }
}
