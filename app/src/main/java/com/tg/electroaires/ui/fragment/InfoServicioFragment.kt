package com.tg.electroaires.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient
import com.tg.electroaires.io.RetrofitClient.repuestoApi
import com.tg.electroaires.io.RetrofitClient.servicioApi
import com.tg.electroaires.model.Repuesto
import com.tg.electroaires.model.createRepuesto
import com.tg.electroaires.ui.HomeActivity
import com.tg.electroaires.ui.adapters.RepuestoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    override fun onDestroyView() {
        super.onDestroyView()

        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        activity.setSupportActionBar(null)
        toolbar?.visibility = View.VISIBLE
    }

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

        // Ocultar el toolbar personalizado del HomeActivity
        val homeToolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        homeToolbar.visibility = View.GONE
        // Manejar el evento de hacer clic en el botón de retroceso
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar2)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java) // Reemplaza "TuActivityDestino" con el nombre de tu Activity de destino
            startActivity(intent)
            activity.finish()
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
                val idABuscar = dialogView.findViewById<EditText>(R.id.idRepuesto).text.toString().toInt()
                cargarUnRepuestos(dialogView, idABuscar)
            }

            alertDialogBuilder.setView(dialogView)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val cantidadRepuestoAdd = dialogView.findViewById<EditText>(R.id.cantidaRepuesto).text.toString().toInt()
                    val idABuscar = dialogView.findViewById<EditText>(R.id.idRepuesto).text.toString().toInt()
                    postRepuestoServicio(idABuscar, cantidadRepuestoAdd)
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

        // Sobrescribe el método onBackPressed de la HomeActivity
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Aquí puedes realizar alguna acción adicional si es necesario

                // Recarga el HomeActivity
                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })

        return view
    }

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
                        view?.findViewById<TextView>(R.id.textFechaEntrada)?.text = "${service?.s_fecha_entrada}"
                        if (service?.s_fecha_salida == null){
                            view?.findViewById<TextView>(R.id.textFechaSalida)?.text = ""
                        }else{
                            view?.findViewById<TextView>(R.id.textFechaSalida)?.text = "${service?.s_fecha_salida}"
                        }
                        view?.findViewById<TextView>(R.id.textTotal)?.text = "${service?.s_total}"

                        val recyclerViewDetails = view?.findViewById<RecyclerView>(R.id.recyclerViewDetails)
                        recyclerViewDetails?.layoutManager = LinearLayoutManager(requireActivity())
                        recyclerViewDetails?.adapter = RepuestoAdapter(requireContext(), this@InfoServicioFragment, service?.detalles_servicio ?: emptyList())

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
    }
    // Realiza una solicitud a la API para obtener la información completa del servicio según el ID
    // Puedes usar Retrofit o cualquier otra biblioteca para realizar la solicitud

    // Una vez que obtengas la información completa del servicio, actualiza tu interfaz de usuario en consecuencia
    // Por ejemplo, muestra los detalles del servicio y configura los botones para CRUD

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

    /*private fun cargarRepuestos(dialogView: View) {
        obtenerRepuestos(
            onSuccess = { repuestos ->
                // Lógica para manejar los repuestos obtenidos

                val nombresRepuestos = repuestos.map { it.r_nombre_repuesto }
                val idRepuesto = repuestos.map {it.id}
                val stockRepuesto = repuestos.map {it.r_cantidad}

                val spinner = dialogView.findViewById<Spinner>(R.id.spinner)

                val adapter = ArrayAdapter(
                    dialogView.context,
                    android.R.layout.simple_spinner_item,
                    nombresRepuestos,
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val nombreSeleccionado = nombresRepuestos[position]
                        val idSeleccionado = idRepuesto[position]
                        val stockSeleccionado = stockRepuesto[position]
                        dialogView.findViewById<TextView>(R.id.idRepuesto).text = "$idSeleccionado"
                        dialogView.findViewById<TextView>(R.id.idAdd).text = "$idSeleccionado"
                        dialogView.findViewById<TextView>(R.id.nombreAdd).text = "$nombreSeleccionado"
                        dialogView.findViewById<TextView>(R.id.cantidadAdd).text = "$stockSeleccionado"
                        // Realiza la acción deseada con el nombre seleccionado en el Spinner
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Maneja el caso en que no se seleccione ningún elemento del Spinner
                    }
                }
            },
            onError = {
                // Lógica para manejar el error en la obtención de los repuestos
            }
        )
    }*/

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



}