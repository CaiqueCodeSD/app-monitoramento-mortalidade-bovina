package com.example.myfirstapp.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.myfirstapp.model.Registro

class DiffCallback : DiffUtil.ItemCallback<Registro>() {

    override fun areItemsTheSame(oldItem: Registro, newItem: Registro): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Registro, newItem: Registro): Boolean {
        return oldItem == newItem
    }
}