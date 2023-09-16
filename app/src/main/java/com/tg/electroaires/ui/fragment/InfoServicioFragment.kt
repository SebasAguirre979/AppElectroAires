package com.tg.electroaires.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient
import com.tg.electroaires.io.RetrofitClient.servicioApi
import com.tg.electroaires.io.RetrofitClient.valoracionApi
import com.tg.electroaires.model.PostValoraciones
import com.tg.electroaires.model.Repuesto
import com.tg.electroaires.model.createRepuesto
import com.tg.electroaires.model.putServicio
import com.tg.electroaires.ui.HomeActivity
import com.tg.electroaires.ui.adapters.RepuestoAdapter
import com.tg.electroaires.ui.adapters.RepuestoResumenAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class InfoServicioFragment : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_info_servicio, container, false)

        //Llamar la toolbar2 de este fragment en especifico
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(view.findViewById(R.id.toolbar2))
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setTitle("Información servicio")

        // Ocultar el toolbar personalizado del HomeActivity
        val homeToolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        homeToolbar.visibility = View.GONE
        // Manejar el evento de hacer clic en el botón de retroceso
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar2)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Limpia la pila de actividades
            startActivity(intent)
        }

        // Llama a la función para actualizar los datos iniciales
        datosServicio()



        // Enlazamos el actualizar al fragmente
        swipeRefreshLayout = view.findViewById(R.id.refreshInfoServicio)
        swipeRefreshLayout.setOnRefreshListener {
            // Aquí se realiza la acción de actualización del Fragment, por ejemplo, cargar nuevos datos desde una fuente externa o realizar algún procesamiento
            datosServicio()

            // Cuando la actualización se complete, asegúrate de detener el indicador de progreso
            swipeRefreshLayout.isRefreshing = false
        }


        val addRepuesto: FloatingActionButton = view.findViewById(R.id.addRepuesto)
        addRepuesto.setOnClickListener {
            // Crear un layout personalizado para el cuadro de diálogo
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_repuesto, null)
            // Obtener referencias a los elementos del layout
            val alertDialogBuilder  = AlertDialog.Builder(context)
            //cargarRepuestos(dialogView)

            val botonBuscar = dialogView.findViewById<ImageButton>(R.id.buscarRepuestoBoton)
            botonBuscar.setOnClickListener{
                val idABuscarText = dialogView.findViewById<EditText>(R.id.idRepuesto).text.toString()

                if (idABuscarText.isNotEmpty()) {
                    val idABuscar = idABuscarText.toInt()
                    cargarUnRepuestos(dialogView, idABuscar)
                } else {
                    Toast.makeText(context, "Por favor, complete el campo con un ID de repuesto", Toast.LENGTH_SHORT).show()
                }
            }

            alertDialogBuilder.setView(dialogView)
                .setPositiveButton("Aceptar", null)
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

            // Mostrar el cuadro de diálogo
            val alertDialog = alertDialogBuilder.create()

            alertDialog.setOnShowListener {
                val acceptButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                acceptButton.setOnClickListener {
                    val cantidadRepuestoAddText = dialogView.findViewById<EditText>(R.id.cantidaRepuesto).text.toString()
                    val idABuscarText = dialogView.findViewById<EditText>(R.id.idRepuesto).text.toString()

                    if (cantidadRepuestoAddText.isNotEmpty() && idABuscarText.isNotEmpty()) {
                        val cantidadRepuestoAdd = cantidadRepuestoAddText.toInt()
                        val idABuscar = idABuscarText.toInt()
                        postRepuestoServicio(idABuscar, cantidadRepuestoAdd)
                        alertDialog.dismiss() // Cerrar manualmente el cuadro de diálogo
                    } else {
                        Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            /*alertDialogBuilder.setView(dialogView)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val cantidadRepuestoAddText = dialogView.findViewById<EditText>(R.id.cantidaRepuesto).text.toString()
                    val idABuscarText = dialogView.findViewById<EditText>(R.id.idRepuesto).text.toString()

                    if (cantidadRepuestoAddText.isNotEmpty() && idABuscarText.isNotEmpty()) {
                        val cantidadRepuestoAdd = cantidadRepuestoAddText.toInt()
                        val idABuscar = idABuscarText.toInt()
                        postRepuestoServicio(idABuscar, cantidadRepuestoAdd)
                        dialog.dismiss() // Cerrar el cuadro de diálogo
                    } else {
                        Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    }

                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()

                }*/

            alertDialog.show()
            // Lógica para eliminar el detalle del servicio
            // Puedes mostrar un cuadro de diálogo de confirmación antes de eliminarlo
        }

        //Sobrescribe el método onBackPressed de la HomeActivity
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Aquí puedes realizar alguna acción adicional si es necesario

                // Recarga el HomeActivity
                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })

        // Boton finalizar servicio
        view.findViewById<Button>(R.id.finalizarBoton).setOnClickListener {
            //Abrir dialog valoraciones
            val dialogViewFinalizarManoObra = LayoutInflater.from(context).inflate(R.layout.dialog_finalizar_servicio_campo_manoobra, null)
            // Crear un layout para el cuadro de diálogo
            val alertDialogBuilderFinalizarManoObra = AlertDialog.Builder(context)
            val manoObraAnterior = view?.findViewById<TextView>(R.id.textManoObra)?.text
            val manoObra = dialogViewFinalizarManoObra.findViewById<EditText>(R.id.nuevoCostoManoDeObra)
            manoObra.setText(manoObraAnterior)

            alertDialogBuilderFinalizarManoObra.setView(dialogViewFinalizarManoObra)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val nuevoManoObraText = manoObra.text.toString()

                    if (nuevoManoObraText.isNotEmpty()){
                        val nuevoManoObra = nuevoManoObraText.toDouble()
                        // Realizar la solicitud de eliminar a la API
                        finalizarServicio(nuevoManoObra)
                        dialog.dismiss() // Cerrar el cuadro de diálogo

                        //Abrir dialog valoraciones
                        val dialogViewValoraciones = LayoutInflater.from(context).inflate(R.layout.dialog_valoraciones, null)
                        // Obtener referencias a los elementos del layout
                        val alertDialogBuilderValoraciones  = AlertDialog.Builder(context)

                        alertDialogBuilderValoraciones.setView(dialogViewValoraciones)
                            .setPositiveButton("Aceptar") { dialog, _ ->
                                val calificacion = dialogViewValoraciones.findViewById<RatingBar>(R.id.ratingBarValoracion).rating.toInt()
                                val descripcionValoracion = dialogViewValoraciones.findViewById<EditText>(R.id.descripcionValoracion).text.toString()
                                // Obtén el ID del servicio pasado desde el adaptador
                                val serviceId = arguments?.getInt("serviceId")?:0

                                Log.d("Valoracion", "cal: $calificacion desc:$descripcionValoracion ser:$serviceId")

                                postValoracionServicio(calificacion, descripcionValoracion, serviceId)
                                resumenServicio()
                                dialog.dismiss() // Cerrar el cuadro de diálogo


                            }
                            .setNegativeButton("Cancelar") { dialog, _ ->
                                resumenServicio()
                                dialog.dismiss()
                            }
                        // Mostrar el cuadro de diálogo
                        val alertDialog = alertDialogBuilderValoraciones.create()
                        alertDialog.show()

                    } else{
                        Toast.makeText(context, "Por favor, ingrese un valor valido", Toast.LENGTH_LONG).show()
                    }

            }
            alertDialogBuilderFinalizarManoObra.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Cerrar el cuadro de diálogo


            }
            val alertDialog = alertDialogBuilderFinalizarManoObra.create()
            alertDialog.show()
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun datosServicio() {
        // Obtén el ID del servicio pasado desde el adaptador
        val serviceId = arguments?.getInt("serviceId")?:0
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = servicioApi.getServiceById(serviceId)
                if (response.isSuccessful) {
                    val service = response.body()
                    // Actualiza la interfaz de usuario con la información del servicio obtenida
                    withContext(Dispatchers.Main) {
                        view?.findViewById<TextView>(R.id.textCliente)?.text = "${service?.cliente?.nombre}"
                        view?.findViewById<TextView>(R.id.textCedula)?.text = "${service?.cliente?.cedula}"
                        view?.findViewById<TextView>(R.id.textCelular)?.text = "${service?.cliente?.celular}"
                        view?.findViewById<TextView>(R.id.textDescripcion)?.text = "${service?.s_descripcion}"
                        view?.findViewById<TextView>(R.id.textPlaca)?.text = "${service?.s_vehiculo?.placa}"
                        view?.findViewById<TextView>(R.id.textTipo)?.text = "${service?.s_vehiculo?.tipo}"
                        view?.findViewById<TextView>(R.id.textManoObra)?.text = "${service?.s_mano_obra}"

                        view?.findViewById<TextView>(R.id.textFechaEntrada)?.text = convertirHora(service?.s_fecha_entrada.toString())

                        if (service?.s_fecha_salida == null){
                            view?.findViewById<TextView>(R.id.textFechaSalida)?.text = "Pendiente"
                        }else{
                            view?.findViewById<TextView>(R.id.textFechaSalida)?.text = convertirHora(service.s_fecha_salida.toString())
                        }

                        view?.findViewById<TextView>(R.id.textTotal)?.text = "${service?.s_total}"

                        val recyclerViewDetails = view?.findViewById<RecyclerView>(R.id.recyclerViewDetails)
                        recyclerViewDetails?.layoutManager = LinearLayoutManager(requireActivity())
                        recyclerViewDetails?.adapter = RepuestoAdapter(requireContext(), this@InfoServicioFragment, service?.detalles_servicio ?: emptyList())

                    }
                } else {
                    // Maneja el caso de error en la respuesta de la API
                }
            } catch (e: Exception) {
                // Maneja errores de red u otros errores en la solicitud a la API
            }
        }
    }

    fun obtenerRepuestos(
        onSuccess: (List<Repuesto>) -> Unit,
        onError: () -> Unit
        ){
        val apiService = RetrofitClient.repuestoApi

        val call = apiService.getRepuestos()
        call.enqueue(object : Callback<List<Repuesto>> {
            override fun onResponse(call: Call<List<Repuesto>>, response: Response<List<Repuesto>>) {
                if (response.isSuccessful) {
                    val repuestos = response.body()
                    if (repuestos != null) {
                        onSuccess(repuestos)
                    }
                } else {
                        onError()// Maneja los casos de error
                }
            }

            override fun onFailure(call: Call<List<Repuesto>>, t: Throwable) {
                // Maneja los casos de error
                onError()
            }
        })
    }

    private fun cargarUnRepuestos(dialogView: View, id: Int) {
        obtenerRepuestos(
            onSuccess = { repuestos ->
                // Lógica para manejar los repuestos obtenidos
                val repuesto = repuestos.find {it.id == id}

                if (repuesto != null) {
                    val nombreRepuesto = repuesto.r_nombre_repuesto
                    val stockRepuesto = repuesto.r_cantidad
                    val precioRepuesto = repuesto.r_valor_publico

                    dialogView.findViewById<TextView>(R.id.nombreAdd).text = nombreRepuesto
                    dialogView.findViewById<TextView>(R.id.disponiblesAdd).text = "$stockRepuesto"
                    dialogView.findViewById<TextView>(R.id.precioAdd).text = "$precioRepuesto"

                    // Utiliza los datos obtenidos como necesites

                } else {
                    // No se encontró ningún repuesto con el ID especificado
                }



            },
            onError = {
                // Lógica para manejar el error en la obtención de los repuestos
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun postRepuestoServicio(idRepuestoAdd: Int, cantidad: Int){
        val data = createRepuesto(idRepuestoAdd, cantidad)
        // Obtén el ID del servicio pasado desde el adaptador
        val serviceId = arguments?.getInt("serviceId")?:0

        val call = servicioApi.addRpuestoServicio(serviceId, data)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // El servicio se eliminó correctamente (estatus 200)
                    // Realizar las acciones necesarias en tu app

                    Toast.makeText(context, "El servicio se agrego correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    // Ocurrió un error al eliminar el servicio
                    Toast.makeText(context, "Error al agregar el servicio", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Error de conexión u otros errores de red
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
        datosServicio()
    }

    private fun finalizarServicio(manoObra: Double){
        val descripcion = view?.findViewById<TextView>(R.id.textDescripcion)?.text.toString()
        val estado = false
        val cliente = view?.findViewById<TextView>(R.id.textCedula)?.text.toString()
        val vehiculo = view?.findViewById<TextView>(R.id.textPlaca)?.text.toString()

        // Obtén el ID del servicio pasado desde el adaptador
        val serviceId = arguments?.getInt("serviceId")?:0
        val data = putServicio(descripcion, manoObra, estado, cliente, vehiculo)

        Log.d("PutServicio", "$data")

        val call = servicioApi.putServicio(serviceId, data)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // El servicio se eliminó correctamente (estatus 200)
                    // Realizar las acciones necesarias en tu app

                    Toast.makeText(context, "El servicio se finalizo correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    // Ocurrió un error al eliminar el servicio
                    Log.d("ERRORESTE", response.message())
                    Toast.makeText(context, "Error al finalizar el servicio", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Error de conexión u otros errores de red
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun  postValoracionServicio(calificacion: Int, descripcionValoracion: String, serviceId: Int){

        val data = PostValoraciones(calificacion, descripcionValoracion, serviceId)
        Log.d("postValoracionServicio", "$data")

        val call = valoracionApi.postValoracion(data)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // El servicio se eliminó correctamente (estatus 200)
                    // Realizar las acciones necesarias en tu app

                    Log.d("postValoracionServicio", response.message())
                    Toast.makeText(context, "La valoracion se guardo correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    // Ocurrió un error al eliminar el servicio
                    Toast.makeText(context, "Error al guardar la valoracion", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Error de conexión u otros errores de red
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun resumenServicio(){
        //Abrir dialog valoraciones
        val dialogViewResumen = LayoutInflater.from(context).inflate(R.layout.dialog_finalizar_servicio, null)
        // Obtener referencias a los elementos del layout
        val alertDialogBuilderResumen  = AlertDialog.Builder(context)

        // Obtén el ID del servicio pasado desde el adaptador
        val serviceId = arguments?.getInt("serviceId")?:0
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = servicioApi.getServiceById(serviceId)
                if (response.isSuccessful) {
                    val service = response.body()
                    // Actualiza la interfaz de usuario con la información del servicio obtenida
                    withContext(Dispatchers.Main) {
                        dialogViewResumen?.findViewById<TextView>(R.id.textCliente2)?.text = "${service?.cliente?.nombre}"
                        dialogViewResumen?.findViewById<TextView>(R.id.textCedula2)?.text = "${service?.cliente?.cedula}"
                        dialogViewResumen?.findViewById<TextView>(R.id.textDescripcion2)?.text = "${service?.s_descripcion}"
                        dialogViewResumen?.findViewById<TextView>(R.id.textPlaca2)?.text = "${service?.s_vehiculo?.placa}"
                        dialogViewResumen?.findViewById<TextView>(R.id.textManoObra2)?.text = "${service?.s_mano_obra}"
                        dialogViewResumen?.findViewById<TextView>(R.id.textFechaEntrada2)?.text = convertirHora(service?.s_fecha_entrada.toString())
                        if (service?.s_fecha_salida == null){
                            dialogViewResumen?.findViewById<TextView>(R.id.textFechaSalida2)?.text = "Pendiente"
                        }else{
                            dialogViewResumen?.findViewById<TextView>(R.id.textFechaSalida2)?.text = convertirHora(service.s_fecha_salida.toString())
                        }
                        dialogViewResumen?.findViewById<TextView>(R.id.textTotal2)?.text = "${service?.s_total}"

                        val recyclerViewDetails = dialogViewResumen?.findViewById<RecyclerView>(R.id.recyclerViewDetails2)
                        recyclerViewDetails?.layoutManager = LinearLayoutManager(requireActivity())
                        recyclerViewDetails?.adapter = RepuestoResumenAdapter(requireContext(), this@InfoServicioFragment, service?.detalles_servicio ?: emptyList())

                        // Actualiza la interfaz de usuario en el hilo principal
                        // por ejemplo, muestra los detalles del servicio y configura los botones CRUD

                    }
                } else {
                    // Maneja el caso de error en la respuesta de la API
                }
            } catch (e: Exception) {
                // Maneja errores de red u otros errores en la solicitud a la API
            }
        }

        alertDialogBuilderResumen.setView(dialogViewResumen)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(activity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Limpia la pila de actividades
                startActivity(intent)

            }
        // Mostrar el cuadro de diálogo
        val alertDialog = alertDialogBuilderResumen.create()
        alertDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertirHora(fecha: String): String? {
        val fechaOriginal = fecha
        val formatoOriginal = DateTimeFormatter.ISO_DATE_TIME
        val fecha = LocalDateTime.parse(fechaOriginal, formatoOriginal)

        val zonaHorariaAPI = ZoneId.of("UTC")
        val zonaHorariaLocal = ZoneId.systemDefault()

        val fechaAjustada = fecha.atZone(zonaHorariaAPI).withZoneSameInstant(zonaHorariaLocal).toLocalDateTime().minusHours(5)
        //val fechaAjustada = ZonedDateTime.of(fecha, zonaHorariaAPI).withZoneSameInstant(zonaHorariaLocal).toLocalDateTime()

        val formatoDeseado = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val fechaFormateada = fechaAjustada.format(formatoDeseado)
        return fechaFormateada
    }

}