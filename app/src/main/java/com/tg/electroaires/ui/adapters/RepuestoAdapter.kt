package com.tg.electroaires.ui.adapters

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tg.electroaires.R
import com.tg.electroaires.io.RetrofitClient.servicioApi
import com.tg.electroaires.model.DetalleServicio
import com.tg.electroaires.model.UpdateRepuesto
import com.tg.electroaires.ui.fragment.InfoServicioFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepuestoAdapter(private val context: Context,
                      private val fragment: InfoServicioFragment,
                      private val details: List<DetalleServicio>) :
    RecyclerView.Adapter<RepuestoAdapter.ServiceDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repuesto, parent, false)
        return ServiceDetailViewHolder(itemView)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ServiceDetailViewHolder, position: Int) {
        val detail = details[position]
        holder.txtRepuesto.text = "Repuesto: ${detail.r_nombre_repuesto}"
        holder.txtCantidad.text = "Valor: ${detail.ds_valor_publico}"
        holder.txtValor.text = "Cantidad: ${detail.s_cantidad}"

        // Agregar el clic del botón Edit
        holder.itemView.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
            // Crear un layout para el cuadro de diálogo
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            .setTitle("Confirmación")
            .setMessage("¿Estás seguro de que deseas eliminar este repuesto?")
            .setPositiveButton("Sí") { dialog, _ ->

                // Realizar la solicitud de eliminar a la API
                solicitarDelete(detail.id)

                dialog.dismiss() // Cerrar el cuadro de diálogo
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Cerrar el cuadro de diálogo
            }
            .create()
            alertDialogBuilder.show()
        }

        // Agregar el clic del botón Delete
        holder.btnEdit.setOnClickListener {
            // Crear un layout personalizado para el cuadro de diálogo
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_edit_repuesto, null)
            // Obtener referencias a los elementos del layout
            val editTextNewCantidad = dialogView.findViewById<EditText>(R.id.editTextNewCantidad)
            val alertDialogBuilder  = AlertDialog.Builder(context)

            alertDialogBuilder.setView(dialogView)
                .setPositiveButton("Aceptar", null)
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

            // Mostrar el cuadro de diálogo
            val alertDialog = alertDialogBuilder.create()

            // Sobrescribir el OnClickListener del botón "Aceptar"
            alertDialog.setOnShowListener {
                val acceptButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                acceptButton.setOnClickListener {
                    val valorIngresadoText = editTextNewCantidad.text.toString()

                    if (valorIngresadoText.isNotEmpty()) {
                        val valorIngresado = valorIngresadoText.toInt()
                        solicitarEdicion((detail.id), valorIngresado)
                        alertDialog.dismiss() // Cerrar manualmente el cuadro de diálogo
                    } else {
                        Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            /*alertDialogBuilder.setView(dialogView)
                .setPositiveButton("Aceptar") { dialog, _ ->

                    // Realizar acción con el valor ingresado
                    val valorIngresadoText = editTextNewCantidad.text.toString()
                    if (valorIngresadoText.isNotEmpty()) {
                        val valorIngresado = valorIngresadoText.toInt()
                        solicitarEdicion((detail.id), valorIngresado)
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
    }

    override fun getItemCount(): Int {
        return details.size
    }

    inner class ServiceDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtRepuesto: TextView = itemView.findViewById(R.id.txtRepuesto)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
        val txtValor: TextView = itemView.findViewById(R.id.txtValor)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun solicitarDelete(id: Int){
        // Realizar la solicitud de eliminación al API
        val call = servicioApi.deleteRepuestoById(id)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // El servicio se eliminó correctamente (estatus 200)
                    // Realizar las acciones necesarias en tu app

                    Toast.makeText(context, "El repuesto se eliminó correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    // Ocurrió un error al eliminar el servicio
                    Toast.makeText(context, "Error al eliminar el repuesto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Error de conexión u otros errores de red
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
        fragment.datosServicio()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun solicitarEdicion(id:Int, cantidad: Int){
        val data = UpdateRepuesto(cantidad)
        val call = servicioApi.updateRepuestoById(id, data)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // El servicio se eliminó correctamente (estatus 200)
                    // Realizar las acciones necesarias en tu app

                    Toast.makeText(context, "El repuesto se edito correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    // Ocurrió un error al eliminar el servicio
                    Toast.makeText(context, "Error al editar el repuesto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Error de conexión u otros errores de red
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
        fragment.datosServicio()
        Log.d("EJCUTANDO LA ACT","OJALA")
    }


}

