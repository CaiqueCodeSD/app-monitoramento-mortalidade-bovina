package com.example.myfirstapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProvider(this).get(RegistroViewModel::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RegistroAdapter(viewModel.listaRegistros)
    }
}

val listaRegistros = listOf(
    Registro("31/03/2026", "Doença", "Animal apresentou sintomas"),
    Registro("30/03/2026", "Acidente", "Queda em terreno irregular"),
    Registro("29/03/2026", "Intoxicação", "Consumo de planta tóxica")
)