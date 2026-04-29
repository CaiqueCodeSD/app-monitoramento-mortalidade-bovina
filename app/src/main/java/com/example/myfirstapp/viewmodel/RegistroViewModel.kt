package com.example.myfirstapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.repository.RegistroRepository
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
    private val repository: RegistroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistroUiState>(RegistroUiState.Loading)
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun carregarRegistros() {
        viewModelScope.launch {
            _uiState.value = RegistroUiState.Loading

            try {
                val lista = repository.buscarRegistros()
                _uiState.value = RegistroUiState.Success(lista)

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