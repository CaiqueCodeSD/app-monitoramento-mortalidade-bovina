package com.example.myfirstapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.R
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.repository.RegistroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class RegistroUiState {
    object Loading : RegistroUiState()
    data class Success(val data: List<Registro>) : RegistroUiState()
    data class Error(val messageResId: Int) : RegistroUiState()
}

class RegistroViewModel : ViewModel() {

    private val repository = RegistroRepository()

    private val _uiState = MutableStateFlow<RegistroUiState>(RegistroUiState.Loading)
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun carregarRegistros() {
        viewModelScope.launch {

            _uiState.value = RegistroUiState.Loading

            try {
                val registros = repository.buscarRegistros()
                _uiState.value = RegistroUiState.Success(registros)

            } catch (e: Exception) {

                Log.e("ERRO_DEBUG", "Tipo: ${e::class.java}")
                Log.e("ERRO_DEBUG", "Mensagem: ${e.message}")

                val mensagem = when (e) {

                    is UnknownHostException -> R.string.erro_internet

                    is SocketTimeoutException -> R.string.erro_timeout

                    is HttpException -> {
                        when (e.code()) {
                            500 -> R.string.erro_servidor
                            404 -> R.string.erro_generico
                            else -> R.string.erro_generico
                        }
                    }

                    else -> R.string.erro_generico
                }

                _uiState.value = RegistroUiState.Error(mensagem)
            }
        }
    }
}