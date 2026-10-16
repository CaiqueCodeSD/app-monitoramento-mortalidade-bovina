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
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.myfirstapp.model.CategoriaMortalidade
import com.example.myfirstapp.model.SuspeitaMorte

sealed class RegistroUiState {
    object Loading : RegistroUiState()
    data class Success(val data: List<Registro>) : RegistroUiState()
    data class Error(val message: String) : RegistroUiState()
}

enum class OrdemExibicao {
    MAIS_RECENTES,
    MAIS_ANTIGOS
}

data class RegistroFormState(

    val data: String = "",

    val causa: String = "",

    val categoriaSelecionada: CategoriaMortalidade? = null,

    val suspeitaSelecionada: SuspeitaMorte? = null,

    val causaPersonalizada: String = "",

    val observacao: String = "",

    val erroData: String? = null,

    val erroCausa: String? = null,

    val erroObservacao: String? = null,

    val erroFoto: Boolean = false,

    val imageUri: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null,

    val exibindoDialogCamera: Boolean = false,

    val exibindoDialogLocalizacao: Boolean = false,

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

    object AbrirPermissaoLocalizacaoSistema :
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

    private var registrosCarregados: List<Registro> = emptyList()

    private var ordemAtual: OrdemExibicao = OrdemExibicao.MAIS_RECENTES

    fun getOrdemAtual(): OrdemExibicao = ordemAtual

    fun alterarOrdemExibicao(ordem: OrdemExibicao) {
        ordemAtual = ordem
        if (_uiState.value is RegistroUiState.Success) {
            _uiState.value = RegistroUiState.Success(
                ordenarRegistros(registrosCarregados, ordemAtual)
            )
        }
    }

    private fun ordenarRegistros(
        lista: List<Registro>,
        ordem: OrdemExibicao
    ): List<Registro> {
        val formato = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        return when (ordem) {
            OrdemExibicao.MAIS_RECENTES -> {
                lista.sortedWith(
                    compareByDescending<Registro> { registro ->
                        try {
                            formato.parse(registro.data)
                        } catch (e: Exception) {
                            null
                        }
                    }.thenByDescending { it.id }
                )
            }
            OrdemExibicao.MAIS_ANTIGOS -> {
                lista.sortedWith(
                    compareBy<Registro> { registro ->
                        try {
                            formato.parse(registro.data)
                        } catch (e: Exception) {
                            null
                        }
                    }.thenBy { it.id }
                )
            }
        }
    }

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
                erroFoto
    }

    private fun validarFormulario(
        state: RegistroFormState
    ): RegistroFormState {

        var erroData: String? = null
        var erroCausa: String? = null
        var erroObservacao: String? = null
        var erroFoto = false

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

        return state.copy(
            erroData = erroData,
            erroCausa = erroCausa,
            erroObservacao = erroObservacao,
            erroFoto = erroFoto
        )
    }

    fun onPermissaoCameraConcedida() {
        _formState.value = _formState.value.copy(
            exibindoDialogCamera = true
        )
    }

    fun onConfirmarDialogCamera() {
        Log.d("CameraDebug", "onConfirmarDialogCamera chamado")
        _formState.value = _formState.value.copy(
            exibindoDialogCamera = false
        )
        viewModelScope.launch {
            Log.d("CameraDebug", "Emitindo AbrirCamera")
            _event.emit(
                RegistroEvent.AbrirCamera
            )
        }
    }

    fun onCancelarDialogCamera() {
        _formState.value = _formState.value.copy(
            exibindoDialogCamera = false
        )
    }

    fun solicitarPermissaoLocalizacao() {
        _formState.value = _formState.value.copy(
            exibindoDialogLocalizacao = true
        )
    }

    fun onConfirmarDialogLocalizacao() {
        _formState.value = _formState.value.copy(
            exibindoDialogLocalizacao = false
        )
        viewModelScope.launch {
            _event.emit(
                RegistroEvent.AbrirPermissaoLocalizacaoSistema
            )
        }
    }

    fun onCancelarDialogLocalizacao() {
        _formState.value = _formState.value.copy(
            exibindoDialogLocalizacao = false
        )
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

    fun onFotoCapturada(uri: String) {

        _formState.value =
            _formState.value.copy(
                imageUri = uri
            )

        viewModelScope.launch {

            _event.emit(
                RegistroEvent.SolicitarLocalizacao
            )

        }

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


    fun onCategoriaMortalidadeSelecionada(
        categoria: CategoriaMortalidade
    ) {
        val state = _formState.value

        if (state.categoriaSelecionada == categoria) {
            // Recolhe a categoria sem perder a seleção atual.
            _formState.value = state.copy(
                categoriaSelecionada = null
            )
        } else {
            // Abre a nova categoria e limpa a suspeita anterior.
            _formState.value = state.copy(
                categoriaSelecionada = categoria,
                suspeitaSelecionada = null,
                causa = "",
                causaPersonalizada = "",
                erroCausa = null
            )
        }
    }


    fun onSuspeitaMorteSelecionada(
        suspeita: SuspeitaMorte
    ) {
        val causa = when (suspeita) {
            SuspeitaMorte.OUTRO -> ""
            else -> suspeita.descricao
        }

        _formState.value = _formState.value.copy(
            suspeitaSelecionada = suspeita,
            causa = causa,
            causaPersonalizada = "",
            erroCausa = null
        )
    }

    fun onCausaPersonalizadaChanged(
        causaPersonalizada: String
    ) {
        val state = _formState.value

        val causa = if (
            state.suspeitaSelecionada == SuspeitaMorte.OUTRO
        ) {
            if (causaPersonalizada.isBlank()) {
                ""
            } else {
                "Outro: ${causaPersonalizada.trim()}"
            }
        } else {
            state.causa
        }

        _formState.value = state.copy(
            causaPersonalizada = causaPersonalizada,
            causa = causa,
            erroCausa = null
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

                registrosCarregados = lista
                val listaOrdenada = ordenarRegistros(registrosCarregados, ordemAtual)

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