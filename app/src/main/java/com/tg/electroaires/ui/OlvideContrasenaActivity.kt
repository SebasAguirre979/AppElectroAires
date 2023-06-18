package com.tg.electroaires.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.tg.electroaires.R

class OlvideContrasenaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_olvide_contrasena)

        //Habilita la flecha de retroceso en el ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val botonEnviarOlvide = findViewById<Button>(R.id.buttonEnviarOlvide)
        val inputOlvide = findViewById<TextInputEditText>(R.id.inputEmailOlvide)

        botonEnviarOlvide.setOnClickListener{
            val username = inputOlvide.text.toString()
            Log.d("MiApp", "El valor de username es: " + username.toString())
            if (username == "admin@gmail.com") {
                Toast.makeText(this, "Se envio correctamente el e-mail", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "E-mail no registrado, intente de nuevo!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}