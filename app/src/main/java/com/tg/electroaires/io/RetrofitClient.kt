package com.tg.electroaires.io
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "http://electroaires.herokuapp.com/" // Reemplaza con la dirección de tu API

    private val retrofit: Retrofit by lazy {
        try {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } catch (e: Exception) {
            Log.e("API", "Error al conectar con el API")
            // Manejar la excepción aquí, como mostrar un mensaje de error o realizar alguna acción de recuperación
            throw e
        }
    }

    val usuarioApi: UsuarioApi by lazy {
        retrofit.create(UsuarioApi::class.java)
    }

    val servicioApi: ServicioApi by lazy {
        retrofit.create(ServicioApi::class.java)
    }

    val repuestoApi: RepuestoApi by lazy {
        retrofit.create(RepuestoApi::class.java)
    }

    val valoracionApi: ValoracionApi by lazy {
        retrofit.create(ValoracionApi::class.java)
    }

    val clienteApi: ClienteApi by lazy {
        retrofit.create(ClienteApi::class.java)
    }

    val vehiculoApi: VehiculoApi by lazy {
        retrofit.create(VehiculoApi::class.java)
    }

    val tokenApi: TokenApi by lazy {
        retrofit.create(TokenApi::class.java)
    }


}

