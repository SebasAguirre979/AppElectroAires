package com.tg.electroaires.ui.fragment

import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient
import com.tg.electroaires.model.Servicio
import com.tg.electroaires.model.ServicioCompleto
import com.tg.electroaires.model.Valoraciones
import com.tg.electroaires.ui.adapters.BusquedaServicioAdapter
import com.tg.electroaires.ui.adapters.ServicioAdapter
import com.tg.electroaires.ui.adapters.ValoracionAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuscarVehiculoFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var busquedaServicioAdapter: BusquedaServicioAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Establecer el título de la ActionBar
        (activity as AppCompatActivity).supportActionBar?.setTitle("Servicios por placa")

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_buscar_vehiculo, container, false)

        // Llamamos el recyclerView
        recyclerView = view.findViewById(R.id.recyclerViewBuscarVehiculo)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Escondo la barra de carga para solo mostrarla al hacer la peticion
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarBuscarVehiculo)
        progressBar.visibility = View.GONE

        val botonBusqueda = view.findViewById<ImageButton>(R.id.busquedaPlaca)

        botonBusqueda.setOnClickListener{
            val placaABuscar = view.findViewById<TextInputEditText>(R.id.editTextBusquedaPlaca).text.toString()

            if (placaABuscar.isNotEmpty() && placaABuscar.length == 6){
                // Hacemos el llamado a la funcion de listar
                ListarServicios(placaABuscar)
                vaciarCampos()
            }else{
                Toast.makeText(context, "Agrega una placa valida", Toast.LENGTH_SHORT).show()
            }

        }

        // Enlazamos el actualizar al fragmente
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshBuscarVehiculo)
        swipeRefreshLayout.setOnRefreshListener {
            // Aquí se realiza la acción de actualización del Fragment, por ejemplo, cargar nuevos datos desde una fuente externa o realizar algún procesamiento
            //ListarServicios(placaABuscar)

            // Cuando la actualización se complete, asegúrate de detener el indicador de progreso
            swipeRefreshLayout.isRefreshing = false
        }


        setHasOptionsMenu(true)

        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // Ocultar el ítem de búsqueda en el Toolbar
        menu.findItem(R.id.buscar)?.isVisible = false
    }

    private fun ListarServicios(id: String){
        //Defino la visibilidad de la barra de carga mientras intenta la peticion
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBarBuscarVehiculo)
        progressBar?.visibility = View.VISIBLE

        // Llamada a la API para obtener los servicios
        val apiService = RetrofitClient.servicioApi
        val call = apiService.getServicioPlaca(id)
        call.enqueue(object : Callback<List<ServicioCompleto>> {
            override fun onResponse(call: Call<List<ServicioCompleto>>, response: Response<List<ServicioCompleto>>) {
                if (response.isSuccessful) {
                    val servicios: List<ServicioCompleto>? = response.body()
                    if (servicios != null) {
                        val listaFiltrada = servicios.sortedByDescending { it.s_fecha_entrada }

                        //originalServices = listaFiltrada

                        // Configurar y establecer para mostrar si no hay servicios activos
                        val advertenciaMsg: TextView? = view?.findViewById(R.id.AdvertenciaMsgBuscarVehiculo)

                        if (listaFiltrada.isEmpty()) {
                            recyclerView.visibility = View.GONE
                            advertenciaMsg?.visibility = View.VISIBLE
                            progressBar?.visibility = View.GONE
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            advertenciaMsg?.visibility = View.GONE
                            progressBar?.visibility = View.GONE
                        }

                        busquedaServicioAdapter = BusquedaServicioAdapter(listaFiltrada)
                        recyclerView.adapter = busquedaServicioAdapter
                    }
                } else {
                    Toast.makeText(context, "Error al mostrar los servicios de este vehiculo", Toast.LENGTH_SHORT).show()
                    progressBar?.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<ServicioCompleto>>, t: Throwable) {
                // Maneja los casos de error
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                progressBar?.visibility = View.GONE
            }
        })
    }



    fun vaciarCampos() {
        val editText1 = view?.findViewById<EditText>(R.id.editTextBusquedaPlaca)

        // Quita el enfoque del campo de texto
        editText1?.clearFocus()

        // Oculta el teclado
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1?.windowToken, 0)
    }
}