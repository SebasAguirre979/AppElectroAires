package com.tg.electroaires.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient
import com.tg.electroaires.io.RetrofitClient.usuarioApi
import com.tg.electroaires.io.UsuarioApi
import com.tg.electroaires.model.DatosRegistro
import com.tg.electroaires.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val botonIniciarSesion = findViewById<Button>(R.id.buttonIniciarSesion)
        val editTextUsername = findViewById<TextInputEditText>(R.id.textInputEmail)
        val editTextPassword = findViewById<TextInputEditText>(R.id.textInputPassword)


        //Boton con Validacion de datos para iniciar sesion desde el front solamente
        botonIniciarSesion.setOnClickListener {
            val username = editTextUsername.text.toString()
            Log.d("MiApp", "El valor de username es: " + username.toString())
            val password = editTextPassword.text.toString()
            Log.d("MiApp", "El valor de password es: " + password.toString())

            verificarRegistroUsuario(username, password)
        }

        //Boton en texto para olvide mi contraseña
        val textOlvideContrasena = findViewById<TextView>(R.id.textViewRecuperarContrasena)
        textOlvideContrasena.paintFlags = textOlvideContrasena.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        textOlvideContrasena.setOnClickListener {
            val intent = Intent(this@MainActivity, OlvideContrasenaActivity::class.java)
            startActivity(intent)
        }
        textOlvideContrasena.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    textOlvideContrasena.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    textOlvideContrasena.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                }
            }
            false
        }

    }

    private fun verificarRegistroUsuario(username: String, password: String) {
        val datosRegistro = DatosRegistro(username, password)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = usuarioApi.verificarRegistro(datosRegistro)
                if (response.isSuccessful) {
                    val usuarioResponse = response.body()
                    if (usuarioResponse != null) {
                        Log.d("Login", "Nombre: ${usuarioResponse.nombre}")
                        Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    }
                } else {
                    if (response.code() == 400) {
                        Log.d("Login", "Contraseña incorrecta")
                        Toast.makeText(this@MainActivity, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Login", "Usuario no encontrado: ${response.code()}")
                        Toast.makeText(this@MainActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Login", "Error en la solicitud: ${e.message}")
            }
        }
    }


    private fun traerUsuarios(){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val usuarios = usuarioApi.obtenerUsuarios()
                for (usuario in usuarios) {
                    Log.d("Usuarios", "ID: ${usuario.id}, Nombre: ${usuario.nombre}, Correo: ${usuario.correo}")
                }
            } catch (e: Exception) {
                Log.e("Usuarios", "Error al obtener los usuarios: ${e.message}")
            }
        }

    }



}