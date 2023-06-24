package com.tg.electroaires.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient
import com.tg.electroaires.model.Valoraciones
import com.tg.electroaires.ui.adapters.ValoracionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValoracionesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var valoracionAdapter: ValoracionAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Establecer el título de la ActionBar
        (activity as AppCompatActivity).supportActionBar?.setTitle("Valoraciones")

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_valoraciones, container, false)

        // Llamamos el recyclerView
        recyclerView = view.findViewById(R.id.recyclerViewValoraciones)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Hacemos el llamado a la funcion de listar
        ListarValoraciones()

        // Enlazamos el actualizar al fragmente
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshValoraciones)
        swipeRefreshLayout.setOnRefreshListener {
            // Aquí se realiza la acción de actualización del Fragment, por ejemplo, cargar nuevos datos desde una fuente externa o realizar algún procesamiento
            ListarValoraciones()

            // Cuando la actualización se complete, asegúrate de detener el indicador de progreso
            swipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    private fun ListarValoraciones(){
        // Llamada a la API para obtener los servicios
        val apiService = RetrofitClient.valoracionApi
        val call = apiService.getValoraciones()
        call.enqueue(object : Callback<List<Valoraciones>> {
            override fun onResponse(call: Call<List<Valoraciones>>, response: Response<List<Valoraciones>>) {
                if (response.isSuccessful) {
                    val valoraciones: List<Valoraciones>? = response.body()
                    if (valoraciones != null) {
                        // Aquí tienes la lista de respuestas, puedes hacer lo que necesites con ella
                        val listaFiltradaValoraciones = valoraciones.sortedByDescending { it.fecha }
                        valoracionAdapter = ValoracionAdapter(listaFiltradaValoraciones)
                        recyclerView.adapter = valoracionAdapter
                    }

                    Toast.makeText(context, "Servicios listados correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al agregar al mostrar las valoraciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Valoraciones>>, t: Throwable) {
                // Maneja los casos de error
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

}