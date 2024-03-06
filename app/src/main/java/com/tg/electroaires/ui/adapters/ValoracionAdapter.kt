package com.tg.electroaires.ui.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tg.electroaires.R
import com.tg.electroaires.model.Valoraciones
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ValoracionAdapter(private var valoraciones: List<Valoraciones>):
    RecyclerView.Adapter<ValoracionAdapter.ValoracionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValoracionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_valoracion, parent, false)
        return ValoracionViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ValoracionViewHolder, position: Int) {
        val valoraciones = valoraciones[position]
        holder.bind(valoraciones)
    }


    override fun getItemCount(): Int {
        return valoraciones.size
    }

    inner class ValoracionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(valoraciones: Valoraciones) {
            // Enlaza los datos del servicio al cardView
            itemView.findViewById<RatingBar>(R.id.calificacion).rating =  valoraciones.calificacion.toFloat()

            val valoracion = valoraciones.opinion

            if (valoracion.isNotEmpty()){
                itemView.findViewById<TextView>(R.id.opinion).text = valoraciones.opinion
            }else itemView.findViewById<TextView>(R.id.opinion).text = "Sin comentario"

            val fechaOriginal = valoraciones.fecha
            val formatoOriginal = DateTimeFormatter.ISO_DATE_TIME
            val fechaEnZonaHoraria = ZonedDateTime.parse(fechaOriginal, formatoOriginal)

            val formatoDeseado = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val fechaFormateada = fechaEnZonaHoraria.format(formatoDeseado)

            itemView.findViewById<TextView>(R.id.fechaValoracion).text = fechaFormateada
        }
    }

    fun updateData(newValoraciones: List<Valoraciones>) {
        valoraciones = newValoraciones
        notifyDataSetChanged()
    }
}