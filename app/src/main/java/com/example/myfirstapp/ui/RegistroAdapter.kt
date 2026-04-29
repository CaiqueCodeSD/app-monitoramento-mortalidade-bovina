package com.example.myfirstapp.ui

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.model.Registro

class RegistroAdapter :
    ListAdapter<Registro, RegistroAdapter.RegistroViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = getItem(position)

        holder.textData.text = formatarNegrito("Data:", registro.data)
        holder.textCausa.text = formatarNegrito("Suspeita da causa:", registro.causa)
        holder.textObservacao.text =
            formatarNegrito("Observação adicional:", registro.observacao)
    }

    // negrito nos rótulos
    private fun formatarNegrito(label: String, valor: String): SpannableString {
        val texto = "$label $valor"
        val spannable = SpannableString(texto)

        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            label.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textData: TextView = itemView.findViewById(R.id.tvData)
        val textCausa: TextView = itemView.findViewById(R.id.tvCausa)
        val textObservacao: TextView = itemView.findViewById(R.id.tvObservacao)
    }

    class DiffCallback : DiffUtil.ItemCallback<Registro>() {
        override fun areItemsTheSame(oldItem: Registro, newItem: Registro): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Registro, newItem: Registro): Boolean {
            return oldItem == newItem
        }
    }
}