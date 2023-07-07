package com.tg.electroaires.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
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
import com.tg.electroaires.ui.HomeActivity
import com.tg.electroaires.ui.adapters.RepuestoAdapter
import com.tg.electroaires.ui.adapters.RepuestoPasadoAdapter
import com.tg.electroaires.ui.adapters.RepuestoResumenAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class InfoServicioPasadoFragment : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_info_servicio_pasado, container, false)

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


        val addRepuesto: FloatingActionButton = view.findViewById(R.id.addServicioPasado)
        addRepuesto.setOnClickListener {

        }

        setHasOptionsMenu(true)

        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // Ocultar el ítem de búsqueda en el Toolbar
        menu.findItem(R.id.buscar)?.isVisible = false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun datosServicio() {
        // Obtén el ID del servicio pasado desde el adaptador
        val serviceId = arguments?.getInt("serviceId")?:0
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.servicioApi.getServiceById(serviceId)
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

                        if (service?.estado == false){
                            view?.findViewById<TextView>(R.id.textEstado)?.text = "Inactivo"
                        }else{
                            view?.findViewById<TextView>(R.id.textEstado)?.text = "Activo"
                        }

                        view?.findViewById<TextView>(R.id.textFechaEntrada)?.text = convertirHora(service?.s_fecha_entrada.toString())

                        if (service?.s_fecha_salida == null){
                            view?.findViewById<TextView>(R.id.textFechaSalida)?.text = ""
                        }else{
                            view?.findViewById<TextView>(R.id.textFechaSalida)?.text = convertirHora(service.s_fecha_entrada)
                        }

                        view?.findViewById<TextView>(R.id.textTotal)?.text = "${service?.s_total}"

                        val recyclerViewDetails = view?.findViewById<RecyclerView>(R.id.recyclerViewDetailsPasado)
                        recyclerViewDetails?.layoutManager = LinearLayoutManager(requireActivity())
                        recyclerViewDetails?.adapter = RepuestoPasadoAdapter(requireContext(), this@InfoServicioPasadoFragment, service?.detalles_servicio ?: emptyList())

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertirHora(fecha: String): String? {
        val fechaOriginal = fecha
        val formatoOriginal = DateTimeFormatter.ISO_DATE_TIME
        val fecha = LocalDateTime.parse(fechaOriginal, formatoOriginal)

        val zonaHorariaAPI = ZoneId.of("UTC")
        val zonaHorariaLocal = ZoneId.systemDefault()

        val fechaAjustada = ZonedDateTime.of(fecha, zonaHorariaAPI).withZoneSameInstant(zonaHorariaLocal).toLocalDateTime()

        val formatoDeseado = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val fechaFormateada = fechaAjustada.format(formatoDeseado)
        return fechaFormateada
    }

}