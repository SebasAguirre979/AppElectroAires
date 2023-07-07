package com.tg.electroaires.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tg.electroaires.R
import com.tg.electroaires.model.DetalleServicio
import com.tg.electroaires.ui.fragment.InfoServicioPasadoFragment

class RepuestoPasadoAdapter(private val context: Context,
                            private val fragment: InfoServicioPasadoFragment,
                            private val details: List<DetalleServicio>) :
    RecyclerView.Adapter<RepuestoPasadoAdapter.RepuestoPasadoAdapterDetailViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepuestoPasadoAdapterDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repuesto, parent, false)
        return RepuestoPasadoAdapterDetailViewHolder(itemView)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RepuestoPasadoAdapterDetailViewHolder, position: Int) {
        val detail = details[position]
        holder.txtRepuesto.text = "Repuesto: ${detail.r_nombre_repuesto}"
        holder.txtCantidad.text = "Valor: ${detail.r_valor_publico}"
        holder.txtValor.text = "Cantidad: ${detail.s_cantidad}"

        holder.btnDelete.visibility = View.GONE
        holder.btnEdit.visibility = View.GONE

    }

    override fun getItemCount(): Int {
        return details.size
    }

    inner class RepuestoPasadoAdapterDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtRepuesto: TextView = itemView.findViewById(R.id.txtRepuesto)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
        val txtValor: TextView = itemView.findViewById(R.id.txtValor)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
    }
}