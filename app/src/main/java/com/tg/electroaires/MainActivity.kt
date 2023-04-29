package com.tg.electroaires

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
            if (username == "1" && password == "1") {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()

            } else {
                Toast.makeText(this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
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


}