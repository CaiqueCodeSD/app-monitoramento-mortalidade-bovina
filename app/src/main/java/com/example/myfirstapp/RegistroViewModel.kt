package com.example.myfirstapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegistroViewModel : ViewModel() {

    private val dadosMockados = listOf(
        Registro(1, "27/03/2026", "Mordida de cobra", "O animal foi mordido por uma cobra coral enquanto estava fora do pasto."),
        Registro(2, "26/03/2026", "Acidente", "O boi caiu em uma grota."),
        Registro(3, "25/03/2026", "Doença", "O animal apresentou sintomas, foi medicado, mas não resistiu."),
        Registro(4, "24/03/2026", "Desconhecida", "O animal estava em estado avançado de decomposição."),
        Registro(5, "23/03/2026", "Tristeza Parasitária Bovina", "O animal estava infestado por carrapatos e acabou morrendo por anemia.")
    )

    private val _listaRegistros = MutableStateFlow(dadosMockados)

    val listaRegistros: StateFlow<List<Registro>> = _listaRegistros.asStateFlow()
}