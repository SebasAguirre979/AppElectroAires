package com.tg.electroaires.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient
import com.tg.electroaires.model.Servicio
import com.tg.electroaires.ui.adapters.ServicioAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ServicioFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var servicioAdapter: ServicioAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var originalServices: List<Servicio> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Establecer el título de la ActionBar
        (activity as AppCompatActivity).supportActionBar?.setTitle("Servicios")

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_servicio, container, false)

        // Llamamos el recyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Hacemos el llamado a la funcion de listar
        ListarServiciosBasico()


        // Enlazamos el actualizar al fragmente
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            // Aquí se realiza la acción de actualización del Fragment, por ejemplo, cargar nuevos datos desde una fuente externa o realizar algún procesamiento
            ListarServiciosBasico()

            // Cuando la actualización se complete, asegúrate de detener el indicador de progreso
            swipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    private fun ListarServiciosBasico(){
        // Llamada a la API para obtener los servicios
        val apiService = RetrofitClient.servicioApi
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val servicios = apiService.obtenerServicios()
                //originalServices = servicios

                val listaFiltrada = servicios.filter { elemento -> elemento.estado }.sortedBy { it.s_fecha_entrada }

                originalServices = listaFiltrada

                // Configurar y establecer para mostrar si no hay servicios activos
                val advertenciaMsg: TextView? = view?.findViewById(R.id.AdvertenciaMsg)
                if (listaFiltrada.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    advertenciaMsg?.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    advertenciaMsg?.visibility = View.GONE
                }

                servicioAdapter = ServicioAdapter(listaFiltrada)
                recyclerView.adapter = servicioAdapter
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al obtener los servicios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun filterServicesByClient(query: String) {
        val filteredServices = originalServices.filter { servicio ->
            servicio.s_descripcion.contains(query, ignoreCase = true) or
            servicio.cliente.contains(query, ignoreCase = true) or
            servicio.s_vehiculo.contains(query, ignoreCase = true)
        }
        servicioAdapter.updateData(filteredServices)
    }



}