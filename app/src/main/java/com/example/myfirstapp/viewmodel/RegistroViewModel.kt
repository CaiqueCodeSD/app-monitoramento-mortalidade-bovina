package com.example.myfirstapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.repository.RegistroRepositoryInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import kotlinx.coroutines.flow.asSharedFlow

sealed class RegistroUiState {
    object Loading : RegistroUiState()
    data class Success(val data: List<Registro>) : RegistroUiState()
    data class Error(val message: String) : RegistroUiState()
}

data class RegistroFormState(

    val data: String = "",

    val causa: String = "",

    val observacao: String = "",

    val erroData: String? = null,

    val erroCausa: String? = null,

    val erroObservacao: String? = null,

    val erroFoto: Boolean = false,

    val erroLocalizacao: Boolean = false,

    val imageUri: String? = null,

    val pendingPhotoUri: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null,

    )



sealed class RegistroEvent {

    data class SalvarRegistro(
        val registro: Registro
    ) : RegistroEvent()

    object AbrirCamera : RegistroEvent()

    object AbrirDatePicker : RegistroEvent()

    object SolicitarPermissaoCamera :
        RegistroEvent()

    object SolicitarLocalizacao :
        RegistroEvent()
}

class RegistroViewModel(
    private val repository: RegistroRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistroUiState>(RegistroUiState.Loading)
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    private val _event =
        MutableSharedFlow<RegistroEvent>(
            extraBufferCapacity = 1
        )

    val event = _event.asSharedFlow()

    private val _formState =
        MutableStateFlow(
            RegistroFormState()
        )

    val formState:
            StateFlow<RegistroFormState> =
        _formState.asStateFlow()

    private fun criarRegistro(
        state: RegistroFormState
    ): Registro {

        return Registro(
            id = System.currentTimeMillis().toInt(),
            data = state.data,
            causa = state.causa,
            observacao = state.observacao,
            fotoUri = state.imageUri,
            latitude = state.latitude,
            longitude = state.longitude
        )
    }

    private fun RegistroFormState.possuiErro(): Boolean {

        return erroData != null ||
                erroCausa != null ||
                erroObservacao != null ||
                erroFoto ||
                erroLocalizacao
    }

    private fun validarFormulario(
        state: RegistroFormState
    ): RegistroFormState {

        var erroData: String? = null
        var erroCausa: String? = null
        var erroObservacao: String? = null
        var erroFoto = false
        var erroLocalizacao = false

        if (state.data.isBlank()) {
            erroData = "Informe a data"
        }

        if (state.causa.isBlank()) {
            erroCausa = "Informe a suspeita da morte"
        }

        if (state.observacao.isBlank()) {
            erroObservacao =
                "Informe uma observação adicional"
        }

        if (state.imageUri == null) {
            erroFoto = true
        }

        if (
            state.latitude == null ||
            state.longitude == null
        ) {
            erroLocalizacao = true
        }

        return state.copy(
            erroData = erroData,
            erroCausa = erroCausa,
            erroObservacao = erroObservacao,
            erroFoto = erroFoto,
            erroLocalizacao = erroLocalizacao
        )
    }

    fun onPermissaoCameraConcedida() {

        viewModelScope.launch {

            _event.emit(
                RegistroEvent.AbrirCamera
            )
        }
    }

    fun onFotoClicked() {

        viewModelScope.launch {

            _event.emit(
                RegistroEvent.SolicitarPermissaoCamera
            )
        }
    }

    fun onDataClicked() {

        viewModelScope.launch {

            _event.emit(
                RegistroEvent.AbrirDatePicker
            )
        }
    }

    fun onNovaFotoIniciada(uri: String) {

        _formState.value =
            _formState.value.copy(
                pendingPhotoUri = uri
            )
    }

    fun onObservacaoChanged(
        observacao: String
    ) {

        _formState.value =
            _formState.value.copy(
                observacao = observacao
            )
    }

    fun onCausaChanged(causa: String) {

        _formState.value =
            _formState.value.copy(
                causa = causa
            )
    }

    fun onDataChanged(data: String) {

        _formState.value =
            _formState.value.copy(
                data = data
            )
    }

    fun onLocalizacaoObtida(
        latitude: Double,
        longitude: Double
    ) {

        _formState.value =
            _formState.value.copy(
                latitude = latitude,
                longitude = longitude
            )
    }

    fun onFotoCapturada(uri: String) {
        _formState.value =
            _formState.value.copy(
                imageUri = uri,
                pendingPhotoUri = null
            )

        viewModelScope.launch {
            _event.emit(RegistroEvent.SolicitarLocalizacao)
        }
    }

    fun onSalvarClicked() {

        val estadoValidado =
            validarFormulario(
                _formState.value
            )

        _formState.value = estadoValidado

        if (estadoValidado.possuiErro()) {
            return
        }

        val registro =
            criarRegistro(
                estadoValidado
            )

        viewModelScope.launch {

            _event.emit(
                RegistroEvent.SalvarRegistro(
                    registro
                )
            )
        }
    }

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