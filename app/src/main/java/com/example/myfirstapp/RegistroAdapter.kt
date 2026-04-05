package com.example.myfirstapp

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView

class RegistroAdapter(private val lista: List<Registro>) :
    RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    // 1. ViewHolder (representa UM item da lista)
    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvData: TextView = itemView.findViewById(R.id.tvData)
        val tvCausa: TextView = itemView.findViewById(R.id.tvCausa)
        val tvObservacao: TextView = itemView.findViewById(R.id.tvObservacao)
        val imgFoto: ImageView = itemView.findViewById(R.id.imgFoto)
    }

    // 2. Cria o layout do item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    // 3. Liga os dados ao layout
    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = lista[position]
        holder.tvData.text = "Data: ${registro.data}"
        holder.tvCausa.text = "Causa: ${registro.causa}"
        holder.tvObservacao.text = "Observação: ${registro.observacao}"
    }

    // 4. Quantidade de itens
    override fun getItemCount(): Int = lista.size
}