package com.tg.electroaires.ui.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.tg.electroaires.R
import com.tg.electroaires.model.Servicio
import com.tg.electroaires.model.ServicioCompleto
import com.tg.electroaires.ui.fragment.InfoServicioFragment
import com.tg.electroaires.ui.fragment.InfoServicioPasadoFragment
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class BusquedaServicioAdapter(private var services: List<ServicioCompleto>):
    RecyclerView.Adapter<BusquedaServicioAdapter.BusquedaServicioViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusquedaServicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_servicio, parent, false)
        return BusquedaServicioViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: BusquedaServicioAdapter.BusquedaServicioViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
        holder.itemView.setOnClickListener {
            /*val intent = Intent(holder.itemView.context, ServicioDetallesActivity::class.java)
            intent.putExtra("serviceId", service.id) // Pasa el ID del servicio o cualquier otra información que necesites
            holder.itemView.context.startActivity(intent)**/
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            val fragment = InfoServicioPasadoFragment()

            // Pasa los datos necesarios al fragmento utilizando Bundle
            val bundle = Bundle()
            bundle.putInt("serviceId", service.id)
            fragment.arguments = bundle

            // Reemplaza el fragmento actual con el nuevo fragmento
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }
    }


    override fun getItemCount(): Int {
        return services.size
    }

    inner class BusquedaServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(servicio: ServicioCompleto) {
            // Enlaza los datos del servicio al cardView
            itemView.findViewById<TextView>(R.id.descripcion).text = "Descripción: " + servicio.s_descripcion
            itemView.findViewById<TextView>(R.id.manoObra).text = "Mano de Obra: " + servicio.s_mano_obra

            val fechaOriginal = servicio.s_fecha_entrada
            val formatoOriginal = DateTimeFormatter.ISO_DATE_TIME
            val fecha = LocalDateTime.parse(fechaOriginal, formatoOriginal)

            val zonaHorariaAPI = ZoneId.of("UTC")
            val zonaHorariaLocal = ZoneId.systemDefault()

            val fechaAjustada = ZonedDateTime.of(fecha, zonaHorariaAPI).withZoneSameInstant(zonaHorariaLocal).toLocalDateTime()

            val formatoDeseado = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val fechaFormateada = fechaAjustada.format(formatoDeseado)

            itemView.findViewById<TextView>(R.id.fechaIngreso).text = "Fecha Ingreso: $fechaFormateada"


            itemView.findViewById<TextView>(R.id.vehiculo).text = "Vehiculo: " + servicio.s_vehiculo.placa

            itemView.findViewById<TextView>(R.id.cliente).text = "Cliente: " + servicio.cliente.cedula

            itemView.findViewById<TextView>(R.id.total).text = "Total: " + servicio.s_total
        }
    }

}