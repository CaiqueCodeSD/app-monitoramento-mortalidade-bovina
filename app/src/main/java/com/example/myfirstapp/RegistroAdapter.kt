package com.example.myfirstapp

import android.view.*
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class RegistroAdapter :
    ListAdapter<Registro, RegistroAdapter.RegistroViewHolder>(DiffCallback()) {

    // 1 - ViewHolder
    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvData: TextView = itemView.findViewById(R.id.tvData)
        val tvCausa: TextView = itemView.findViewById(R.id.tvCausa)
        val tvObservacao: TextView = itemView.findViewById(R.id.tvObservacao)
        val imgFoto: ImageView = itemView.findViewById(R.id.imgFoto)
    }

    // 2 - Layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    // 3 - Bind
    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = getItem(position)

        holder.tvData.text = "Data: ${registro.data}"
        holder.tvCausa.text = "Causa: ${registro.causa}"
        holder.tvObservacao.text = "Observação: ${registro.observacao}"

    }
}