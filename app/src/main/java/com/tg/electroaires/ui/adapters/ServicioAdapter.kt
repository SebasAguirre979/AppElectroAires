package com.tg.electroaires.ui.adapters
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.tg.electroaires.R
import com.tg.electroaires.model.Servicio
import com.tg.electroaires.ui.fragment.InfoServicioFragment

class ServicioAdapter(private var services: List<Servicio>) :
    RecyclerView.Adapter<ServicioAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_servicio, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
        holder.itemView.setOnClickListener {
            /*val intent = Intent(holder.itemView.context, ServicioDetallesActivity::class.java)
            intent.putExtra("serviceId", service.id) // Pasa el ID del servicio o cualquier otra información que necesites
            holder.itemView.context.startActivity(intent)**/
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            val fragment = InfoServicioFragment()

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

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(servicio: Servicio) {
            // Enlaza los datos del servicio al cardView
            itemView.findViewById<TextView>(R.id.descripcion).text = "Descripción: " + servicio.s_descripcion
            itemView.findViewById<TextView>(R.id.manoObra).text = "Mano de Obra: " + servicio.s_mano_obra
            itemView.findViewById<TextView>(R.id.fechaIngreso).text = "Fecha Ingreso: " + servicio.s_fecha_entrada
            itemView.findViewById<TextView>(R.id.cliente).text = "Cliente: " + servicio.cliente
            itemView.findViewById<TextView>(R.id.vehiculo).text = "Vehiculo: " + servicio.s_vehiculo
            itemView.findViewById<TextView>(R.id.total).text = "Total: " + servicio.s_total
        }
    }

    fun updateData(newServices: List<Servicio>) {
        services = newServices
        notifyDataSetChanged()
    }

}
