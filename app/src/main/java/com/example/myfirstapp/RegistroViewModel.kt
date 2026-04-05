package com.example.myfirstapp

import androidx.lifecycle.ViewModel

class RegistroViewModel : ViewModel() {

    val listaRegistros = listOf(
        Registro("27/03/2026", "Mordida de cobra", "O animal foi mordido por uma cobra coral enquanto estava fora do pasto."),
        Registro("26/03/2026", "Acidente", "O boi caiu em uma gruta."),
        Registro("25/03/2026", "Doença", "O animal apresentou sintomas, foi medicado, mas não resistiu.")
    )
}