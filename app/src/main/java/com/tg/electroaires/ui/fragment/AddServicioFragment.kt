package com.tg.electroaires.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient
import com.tg.electroaires.model.GetCliente
import com.tg.electroaires.model.GetVehiculo
import com.tg.electroaires.model.Servicio
import com.tg.electroaires.model.postServicio
import com.tg.electroaires.model.verificacionCliente
import com.tg.electroaires.model.verificacionVehiculo
import com.tg.electroaires.ui.AdminToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddServicioFragment : Fragment() {

    private var validarBotonCliente: Boolean = false
    private var validarBotonVehiculo: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Establecer el título de la ActionBar
        (activity as AppCompatActivity).supportActionBar?.setTitle("Añadir Servicio")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_servicio, container, false)

        val btnBuscarCliente = view.findViewById<ImageButton>(R.id.busquedaCliente)
        val btnBuscarvehiculo = view.findViewById<ImageButton>(R.id.busquedaVehiculo)
        val btnAddServicio = view.findViewById<FloatingActionButton>(R.id.addServicio)

        btnBuscarCliente.setOnClickListener {
            val textCedulaText = view.findViewById<EditText>(R.id.editTextCliente).text.toString()
            Log.d("VerificaCliente", textCedulaText)

            if (textCedulaText.isNotEmpty()) {
                val textCedula = textCedulaText.toLong()
                verificarCliente(textCedula)
            } else {
                Toast.makeText(context, "Por favor, ingrese una cedula valida", Toast.LENGTH_SHORT).show()
            }

        }

        btnBuscarvehiculo.setOnClickListener {
            val textPlaca = view.findViewById<EditText>(R.id.editTextVehiculo).text.toString()
            Log.d("VerificaCliente", textPlaca)

            if (textPlaca.isNotEmpty() && textPlaca.length == 6) {
                verificarVehiculo(textPlaca)
            } else {
                Toast.makeText(context, "Por favor, ingrese una placa valida", Toast.LENGTH_SHORT).show()
            }

        }

        btnAddServicio.setOnClickListener{
            if (validarBotonCliente && validarBotonVehiculo){
                val textCedula = view.findViewById<EditText>(R.id.editTextCliente).text.toString().toLong()
                val textPlaca = view.findViewById<EditText>(R.id.editTextVehiculo).text.toString()
                val textDescripcion = view.findViewById<EditText>(R.id.descripcionServicio).text.toString()
                val textManoDeObra = view.findViewById<EditText>(R.id.editTextManoObra).text.toString()

                val manoObra = if (textManoDeObra.isNotEmpty()){
                    textManoDeObra.toDouble()
                }else{
                    0.0
                }

                if (textDescripcion == ""){
                    Toast.makeText(context, "Agregue una descripción", Toast.LENGTH_LONG)
                        .show()
                }else{
                    crearServicio(textDescripcion, manoObra, textCedula, textPlaca)
                    Log.d("VerficiarParametro","$validarBotonCliente, $validarBotonVehiculo")
                    vaciarCampos()
                }
            }else{
                Toast.makeText(context, "Valida el usuario y vehiculo primero", Toast.LENGTH_LONG)
                    .show()
                Log.d("VerficiarParametro","$validarBotonCliente, $validarBotonVehiculo")
            }

        }

        setHasOptionsMenu(true)

        return view

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // Ocultar el ítem de búsqueda en el Toolbar
        menu.findItem(R.id.buscar)?.isVisible = false
    }

    private fun verificarCliente(cedula: Long) {

        val data = verificacionCliente(cedula)

        // Obtener el token de acceso
        val accessToken = AdminToken.getAccessToken()

        if (accessToken != null) {
            val call = RetrofitClient.clienteApi.verficacionCliente("Bearer $accessToken", data)
            call.enqueue(object : Callback<GetCliente> {
                override fun onResponse(call: Call<GetCliente>, response: Response<GetCliente>) {
                    if (response.isSuccessful) {
                        // El servicio se eliminó correctamente (estatus 200)
                        // Realizar las acciones necesarias en tu app
                        val cliente = response.body()

                        Log.d("VerificaClienteDatos", cliente.toString())
                        Log.d("VerificaCliente", response.message())

                        view?.findViewById<TextView>(R.id.textNombre)?.text = cliente?.nombre

                        Toast.makeText(context, "El cliente se encuentra registrado", Toast.LENGTH_SHORT)
                            .show()

                        validarBotonCliente = true
                    } else {
                        // Ocurrió un error al eliminar el servicio
                        Toast.makeText(context, "Cliente no encontrado", Toast.LENGTH_LONG)
                            .show()
                        view?.findViewById<TextView>(R.id.textNombre)?.text = ""
                        crearCliente(cedula)
                        Log.d("VerificaCliente", response.message())
                    }
                }

                override fun onFailure(call: Call<GetCliente>, t: Throwable) {
                    // Error de conexión u otros errores de red
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Si no se tiene un token de acceso válido, llamar a generateOrRefreshToken()
            AdminToken.generateOrRefreshToken {
                // Aquí puedes realizar cualquier acción adicional una vez que el token esté listo
                // por ejemplo, volver a llamar a postCambiarContrasena() para reintentar la solicitud
                verificarCliente(cedula)
            }
        }
    }

    private fun crearCliente(cedula: Long) {

        // Crear un layout personalizado para el cuadro de diálogo
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_crear_cliente, null)

        dialogView.findViewById<EditText>(R.id.cedulaCliente).setText("$cedula")

        // Obtener referencias a los elementos del layout
        val alertDialogBuilder  = AlertDialog.Builder(dialogView.context)
        alertDialogBuilder.setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->

                val nombre = dialogView.findViewById<EditText>(R.id.nombreCliente).text.toString()
                dialogView.findViewById<EditText>(R.id.cedulaCliente).setText("$cedula")
                val celular = dialogView.findViewById<EditText>(R.id.celularCliente).text.toString().toLong()

                val data = GetCliente(cedula, nombre, celular)

                // Obtener el token de acceso
                val accessToken = AdminToken.getAccessToken()

                if (accessToken != null) {
                    val call = RetrofitClient.clienteApi.crearCliente("Bearer $accessToken", data)
                    call.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                // El servicio se eliminó correctamente (estatus 200)
                                // Realizar las acciones necesarias en tu app
                                val cliente = response.body()

                                Log.d("CrearCliente", response.message())

                                view?.findViewById<TextView>(R.id.textNombre)?.text

                                Toast.makeText(context, "Cliente guardado correctamente", Toast.LENGTH_SHORT)
                                    .show()

                                verificarCliente(cedula)
                            } else {
                                // Ocurrió un error al eliminar el servicio
                                Toast.makeText(context, "Error al guardar el cliente", Toast.LENGTH_SHORT)
                                    .show()
                                Log.d("CrearCliente", response.message())
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            // Error de conexión u otros errores de red
                            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    // Si no se tiene un token de acceso válido, llamar a generateOrRefreshToken()
                    AdminToken.generateOrRefreshToken {
                        // Aquí puedes realizar cualquier acción adicional una vez que el token esté listo
                        // por ejemplo, volver a llamar a postCambiarContrasena() para reintentar la solicitud
                        crearCliente(cedula)
                    }
                }


                dialog.dismiss() // Cerrar el cuadro de diálogo

            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        // Mostrar el cuadro de diálogo
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        // Lógica para eliminar el detalle del servicio
        // Puedes mostrar un cuadro de diálogo de confirmación antes de eliminarlo

    }

    private fun verificarVehiculo(placa: String) {

        val data = verificacionVehiculo(placa)

        // Obtener el token de acceso
        val accessToken = AdminToken.getAccessToken()

        if (accessToken != null) {
            val call = RetrofitClient.vehiculoApi.verficacionVehiculo("Bearer $accessToken", data)
            call.enqueue(object : Callback<GetVehiculo> {
                override fun onResponse(call: Call<GetVehiculo>, response: Response<GetVehiculo>) {
                    if (response.isSuccessful) {
                        // El servicio se eliminó correctamente (estatus 200)
                        // Realizar las acciones necesarias en tu app
                        val vehiculo = response.body()

                        Log.d("VerificaClienteDatos", vehiculo.toString())
                        Log.d("VerificaCliente", response.message())

                        view?.findViewById<TextView>(R.id.textTipo)?.text = vehiculo?.tipo

                        Toast.makeText(context, "El vehiculo se encuentra registrado", Toast.LENGTH_SHORT)
                            .show()

                        validarBotonVehiculo = true
                    } else {
                        // Ocurrió un error al eliminar el servicio
                        Toast.makeText(context, "Vehiculo no encontrado", Toast.LENGTH_SHORT)
                            .show()
                        view?.findViewById<TextView>(R.id.textTipo)?.text = ""
                        crearVehiculo(placa)
                        Log.d("VerificaCliente", response.message())
                    }
                }

                override fun onFailure(call: Call<GetVehiculo>, t: Throwable) {
                    // Error de conexión u otros errores de red
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Si no se tiene un token de acceso válido, llamar a generateOrRefreshToken()
            AdminToken.generateOrRefreshToken {
                // Aquí puedes realizar cualquier acción adicional una vez que el token esté listo
                // por ejemplo, volver a llamar a postCambiarContrasena() para reintentar la solicitud
                verificarVehiculo(placa)
            }
        }
    }
    private fun crearVehiculo(placa: String) {

        // Crear un layout personalizado para el cuadro de diálogo
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_crear_vehiculo, null)

        dialogView.findViewById<EditText>(R.id.placaVehiculo).setText("$placa")

        // Obtener referencias a los elementos del layout
        val alertDialogBuilder  = AlertDialog.Builder(dialogView.context)
        alertDialogBuilder.setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->

                dialogView.findViewById<EditText>(R.id.placaVehiculo).setText("$placa")
                val tipo = dialogView.findViewById<EditText>(R.id.tipoVehiculo).text.toString()

                val data = GetVehiculo(placa, tipo)

                // Obtener el token de acceso
                val accessToken = AdminToken.getAccessToken()

                if (accessToken != null) {
                    val call = RetrofitClient.vehiculoApi.crearVehiculo("Bearer $accessToken", data)
                    call.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                // El servicio se eliminó correctamente (estatus 200)
                                // Realizar las acciones necesarias en tu app
                                val vehiculo = response.body()

                                Log.d("CrearVehiculo", response.message())

                                view?.findViewById<TextView>(R.id.textNombre)?.text

                                Toast.makeText(context, "Vehiculo guardado correctamente", Toast.LENGTH_SHORT)
                                    .show()

                                verificarVehiculo(placa)
                            } else {
                                // Ocurrió un error al eliminar el servicio
                                Toast.makeText(context, "Error al guardar el vehiculo", Toast.LENGTH_SHORT)
                                    .show()
                                Log.d("CrearVehiculo", response.message())
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            // Error de conexión u otros errores de red
                            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    // Si no se tiene un token de acceso válido, llamar a generateOrRefreshToken()
                    AdminToken.generateOrRefreshToken {
                        // Aquí puedes realizar cualquier acción adicional una vez que el token esté listo
                        // por ejemplo, volver a llamar a postCambiarContrasena() para reintentar la solicitud
                        crearVehiculo(placa)
                    }
                }


                dialog.dismiss() // Cerrar el cuadro de diálogo

            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        // Mostrar el cuadro de diálogo
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        // Lógica para eliminar el detalle del servicio
        // Puedes mostrar un cuadro de diálogo de confirmación antes de eliminarlo

    }

    private fun crearServicio(s_descripcion: String, s_mano_obra: Double, cliente: Long, s_vehiculo: String){

        val data = postServicio(s_descripcion, s_mano_obra, cliente, s_vehiculo)

        val call = RetrofitClient.servicioApi.crearServicio(data)
        call.enqueue(object : Callback<Servicio> {
            override fun onResponse(call: Call<Servicio>, response: Response<Servicio>) {
                if (response.isSuccessful) {
                    // El servicio se eliminó correctamente (estatus 200)
                    // Realizar las acciones necesarias en tu app
                    val servicio = response.body()
                    val idServicio = servicio?.id

                    Log.d("CrearServicio", idServicio.toString())

                    Toast.makeText(context, "Servicio guardado correctamente", Toast.LENGTH_SHORT)
                        .show()

                    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                    val fragment = InfoServicioFragment()

                    // Pasa los datos necesarios al fragmento utilizando Bundle
                    val bundle = Bundle()
                    if (idServicio != null) {
                        bundle.putInt("serviceId", idServicio)
                    }
                    fragment.arguments = bundle

                    // Reemplaza el fragmento actual con el nuevo fragmento
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()

                } else {
                    // Ocurrió un error al eliminar el servicio
                    Toast.makeText(context, "Error al guardar el Servicio", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("CrearServicio", response.message())
                }
            }

            override fun onFailure(call: Call<Servicio>, t: Throwable) {
                // Error de conexión u otros errores de red
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun vaciarCampos() {
        val editText1 = view?.findViewById<EditText>(R.id.editTextCliente)
        val editText2 = view?.findViewById<EditText>(R.id.editTextVehiculo)
        val editText3 = view?.findViewById<EditText>(R.id.descripcionServicio)
        val editText4 = view?.findViewById<EditText>(R.id.editTextManoObra)

        editText1?.setText("")
        editText2?.setText("")
        editText3?.setText("")
        editText4?.setText("")
    }

}