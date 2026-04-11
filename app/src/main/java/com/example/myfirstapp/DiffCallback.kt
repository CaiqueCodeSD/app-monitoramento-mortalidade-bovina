package com.example.myfirstapp
import androidx.recyclerview.widget.DiffUtil

class DiffCallback : DiffUtil.ItemCallback<Registro>() {

    override fun areItemsTheSame(oldItem: Registro, newItem: Registro): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Registro, newItem: Registro): Boolean {
        return oldItem == newItem
    }
}