package com.tg.electroaires.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient.usuarioApi
import com.tg.electroaires.model.DatosLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Variables del diseño para ser tratadas
        val botonIniciarSesion = findViewById<Button>(R.id.buttonIniciarSesion)
        val editTextUsername = findViewById<TextInputEditText>(R.id.textInputEmail)
        val editTextPassword = findViewById<TextInputEditText>(R.id.textInputPassword)

        //Escondo la barra de carga para solo mostrarla al hacer la peticion
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        //Boton con Validacion de datos para iniciar sesion
        botonIniciarSesion.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            // Verificar si el campo cedula esta vacio
            var usernameInput: Long? = null
            if (username.isNotBlank()) {
                usernameInput = username.toLong()
            }

            // Verificar si la cédula es válida
            if (usernameInput != null) {
                // Realizar operaciones con la cédula
                verificarRegistroUsuario(usernameInput, password)
            } else {
                // Manejar el caso de una cédula no válida
                Toast.makeText(this@MainActivity, "Ingresa un valor valido", Toast.LENGTH_SHORT).show()
            }
        }

        /*//Boton en texto para olvide mi contraseña
        val textOlvideContrasena = findViewById<TextView>(R.id.textViewRecuperarContrasena)
        textOlvideContrasena.paintFlags = textOlvideContrasena.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        textOlvideContrasena.setOnClickListener {
            val intent = Intent(this@MainActivity, OlvideContrasenaActivity::class.java)
            startActivity(intent)
        }
        textOlvideContrasena.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    textOlvideContrasena.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    textOlvideContrasena.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                }
            }
            false
        }*/

    }

    //Validacion del usuario desde el API para dar el acceso a la app
    private fun verificarRegistroUsuario(username: Long, password: String) {
        val datosRegistro = DatosLogin(username, password)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                //Defino la visibilidad de la barra de carga mientras intenta la peticion
                val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.VISIBLE
                val response = usuarioApi.verificarRegistro(datosRegistro)
                if (response.isSuccessful) {
                    val usuarioResponse = response.body()
                    if (usuarioResponse != null) {
                        Log.d("Login", "Nombre: ${usuarioResponse.nombre}")
                        val rol = usuarioResponse.rol

                        if(rol == "Admin" ){
                            Toast.makeText(this@MainActivity, "Usuario con rol no autorizado", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }else{
                            Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intent)

                            //Asignar a la variableGlobal el nombre de usuario para mostrar
                            val singleton = VariableGlobal.getInstance()
                            singleton.nombreUsuario = usuarioResponse.nombre
                            singleton.cedula = usuarioResponse.cedula

                            progressBar.visibility = View.GONE
                            finishAffinity()
                        }
                    }
                } else {
                    if (response.code() == 400) {
                        Log.d("Login", "Contraseña incorrecta")
                        Toast.makeText(this@MainActivity, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    } else {
                        Log.e("Login", "Usuario no encontrado: ${response.code()}")
                        Toast.makeText(this@MainActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.e("Login", "Error en la solicitud: ${e.message}")
            }
        }
    }
}