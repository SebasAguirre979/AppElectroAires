package com.tg.electroaires.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient
import com.tg.electroaires.model.CambioContrasena
import com.tg.electroaires.model.putServicio
import com.tg.electroaires.ui.AdminToken
import com.tg.electroaires.ui.VariableGlobal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UsuarioFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Establecer el título de la ActionBar
        (activity as AppCompatActivity).supportActionBar?.setTitle("Cambio de contraseña")

        val view = inflater.inflate(R.layout.fragment_usuario, container, false)

        // Obtener el usuario y cedula desde la VariableGlobal
        val singleton = VariableGlobal.getInstance()
        val nombre_usuario = singleton.nombreUsuario
        val cedula = singleton.cedula

        //Mandar nombre al nav_header
        val textView = view.findViewById<TextView>(R.id.textViewName)
        textView.text = "$nombre_usuario"
        val textView2 = view.findViewById<TextView>(R.id.textViewCedula)
        textView2.text = "$cedula"

        val cambiarContraenaBoton = view.findViewById<Button>(R.id.buttonChangePassword)

        cambiarContraenaBoton.setOnClickListener{
            val anteriorContrasena = view.findViewById<EditText>(R.id.editTextOldPassword).text.toString()
            val nuevaContrasenaInput = view.findViewById<EditText>(R.id.editTextNewPassword).text.toString()
            val confirmacionNuevaContrasenaInput = view.findViewById<EditText>(R.id.editTextConfirmPassword).text.toString()

            Log.d("HOLAAA","$nuevaContrasenaInput, $confirmacionNuevaContrasenaInput")

                if (anteriorContrasena.isNotEmpty() && nuevaContrasenaInput.isNotEmpty() && confirmacionNuevaContrasenaInput.isNotEmpty()){

                    if (nuevaContrasenaInput == confirmacionNuevaContrasenaInput && anteriorContrasena != nuevaContrasenaInput){

                        val alertDialog = AlertDialog.Builder(requireContext())
                            .setMessage("Esta seguro de cambiar la contraseña?")
                            .setPositiveButton("Aceptar") { dialog, _ ->
                                Log.d("PRUEBA","$anteriorContrasena $nuevaContrasenaInput $confirmacionNuevaContrasenaInput")
                                postCambiarContrasena(cedula.toString().toLong(), anteriorContrasena, confirmacionNuevaContrasenaInput)
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){dialog, _ ->
                                dialog.dismiss() // Cerrar el cuadro de diálogo
                            }
                            .create()

                        alertDialog.show()
                    }else{
                        Toast.makeText(context, "La contraseña nueva no coinciden en ambos campos, o estas utilizando la misma contraseña anterior", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(context, "LLene todos los campos para continuar", Toast.LENGTH_SHORT).show()
                }

        }

        /*//PRUEBAS PARA PEDIR TOKEN
        val botonTokenn = view.findViewById<Button>(R.id.buttonToken)
        botonTokenn.setOnClickListener{

            AdminToken.generateOrRefreshToken(){
                Log.d("PETICIONTOKEN","${AdminToken.getAccessToken()}")
            }

        }*/

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return view


    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // Ocultar el ítem de búsqueda en el Toolbar
        menu.findItem(R.id.buscar)?.isVisible = false
    }

    private fun postCambiarContrasena(cedula: Long, anteriorContrasena: String, nuevaContrasena: String){
        // Obtén el ID del servicio pasado desde el adaptador
        val cedulaUsuario = cedula

        val data = CambioContrasena(anteriorContrasena, nuevaContrasena)

        // Obtener el token de acceso
        val accessToken = AdminToken.getAccessToken()

        if (accessToken != null) {
            val call = RetrofitClient.usuarioApi.cambioContrasena("Bearer $accessToken", cedulaUsuario, data)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // El servicio se eliminó correctamente (estatus 200)
                        // Realizar las acciones necesarias en tu app

                        Toast.makeText(
                            context,
                            "La contraseña se cambio correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        vaciarCampos()
                    } else {
                        // Ocurrió un error al eliminar el servicio
                        Toast.makeText(
                            context,
                            "La contraseña antigua no coincide",
                            Toast.LENGTH_SHORT
                        ).show()
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Error de conexión u otros errores de red
                    Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }else{
            // Si no se tiene un token de acceso válido, llamar a generateOrRefreshToken()
            AdminToken.generateOrRefreshToken {
                // Aquí puedes realizar cualquier acción adicional una vez que el token esté listo
                // por ejemplo, volver a llamar a postCambiarContrasena() para reintentar la solicitud
                postCambiarContrasena(cedula, anteriorContrasena, nuevaContrasena)
            }
        }
    }

    fun vaciarCampos() {
        val editText1 = view?.findViewById<EditText>(R.id.editTextConfirmPassword)
        val editText2 = view?.findViewById<EditText>(R.id.editTextNewPassword)
        val editText3 = view?.findViewById<EditText>(R.id.editTextOldPassword)

        editText1?.setText("")
        editText2?.setText("")
        editText3?.setText("")
    }
}