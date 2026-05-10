package com.example.myfirstapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.repository.RegistroRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

sealed class RegistroUiState {
    object Loading : RegistroUiState()
    data class Success(val data: List<Registro>) : RegistroUiState()
    data class Error(val message: String) : RegistroUiState()
}

class RegistroViewModel(
    private val repository: RegistroRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistroUiState>(RegistroUiState.Loading)
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun salvarRegistro(registro: Registro) {

        viewModelScope.launch {

            try {

                Log.d(
                    "REGISTRO_DEBUG",
                    "Salvando registro no ViewModel"
                )

                repository.salvarRegistro(registro)

                carregarRegistros()

            } catch (e: Exception) {

                _uiState.value =
                    RegistroUiState.Error(
                        "Erro ao salvar registro"
                    )
            }
        }
    }

    fun carregarRegistros() {
        viewModelScope.launch {
            _uiState.value = RegistroUiState.Loading

            try {
                val lista = repository.buscarRegistros()

                Log.d(
                    "REGISTRO_DEBUG",
                    "Lista carregada: ${lista.size}"
                )

                val formato =
                    java.text.SimpleDateFormat(
                        "dd/MM/yyyy",
                        java.util.Locale.getDefault()
                    )

                val listaOrdenada =
                    lista.sortedByDescending { registro ->

                        try {

                            formato.parse(registro.data)

                        } catch (e: Exception) {

                            null
                        }
                    }

                _uiState.value =
                    RegistroUiState.Success(listaOrdenada)

            } catch (e: UnknownHostException) {
                _uiState.value = RegistroUiState.Error("Sem conexão com a internet")

            } catch (e: HttpException) {
                _uiState.value = RegistroUiState.Error("Erro no servidor")

            } catch (e: Exception) {
                _uiState.value = RegistroUiState.Error("Erro inesperado")
            }
        }
    }
}