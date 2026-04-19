package com.example.myfirstapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.ui.DiffCallback
import com.example.myfirstapp.R
import com.example.myfirstapp.model.Registro

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
    @SuppressLint("SetTextI18n") //adicionei em 16/04 sob sugestão da IDE
    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = getItem(position)

        holder.tvData.text = "Data: ${registro.data}"
        holder.tvCausa.text = "Suspeita da causa: ${registro.causa}"
        holder.tvObservacao.text = "Observação adicional: ${registro.observacao}"

    }
}