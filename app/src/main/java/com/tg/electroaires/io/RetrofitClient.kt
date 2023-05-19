package com.tg.electroaires.io
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8000/" // Reemplaza con la dirección de tu API

    private val retrofit: Retrofit by lazy {
        try {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } catch (e: Exception) {
            // Manejar la excepción aquí, como mostrar un mensaje de error o realizar alguna acción de recuperación
            throw e
        }
    }

    val usuarioApi: UsuarioApi by lazy {
        retrofit.create(UsuarioApi::class.java)
    }

    /**
     val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8000/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

    val usuarioApi = retrofit.create(UsuarioApi::class.java)
     */



}

