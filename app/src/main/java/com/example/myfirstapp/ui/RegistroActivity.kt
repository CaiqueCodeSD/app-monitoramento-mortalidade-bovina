package com.example.myfirstapp.ui

import android.util.Log
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.ActivityRegistroBinding
import com.example.myfirstapp.viewmodel.RegistroViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.lifecycle.Lifecycle
import androidx.core.widget.doAfterTextChanged
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.viewmodel.RegistroEvent
import com.example.myfirstapp.viewmodel.RegistroFormState
import com.example.myfirstapp.camera.CameraActivity
import android.graphics.Typeface
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.example.myfirstapp.model.CategoriaMortalidade
import com.example.myfirstapp.model.SuspeitaMorte

class RegistroActivity : AppCompatActivity() {

    private val viewModel: RegistroViewModel by viewModel()

    private val cabecalhosCategoria =
        mutableMapOf<CategoriaMortalidade, TextView>()

    private val gruposSuspeitas =
        mutableMapOf<CategoriaMortalidade, RadioGroup>()

    private val radiosSuspeitas =
        mutableMapOf<SuspeitaMorte, RadioButton>()

    private lateinit var binding: ActivityRegistroBinding

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient

    private var dialogCamera: AlertDialog? = null
    private var dialogLocalizacao: AlertDialog? = null

    private fun finalizarRegistro(
        registro: Registro
    ) {

        val resultIntent = Intent()

        resultIntent.putExtra(
            "novo_registro",
            registro
        )

        setResult(
            RESULT_OK,
            resultIntent
        )

        Toast.makeText(
            this,
            "Registro salvo com sucesso!",
            Toast.LENGTH_SHORT
        ).show()


        finish()
    }

    private fun configurarCamposFormulario() {

        binding.etData.doAfterTextChanged {

            viewModel.onDataChanged(
                it?.toString().orEmpty()
            )
        }


        binding.etCausaPersonalizada.doAfterTextChanged {
            viewModel.onCausaPersonalizadaChanged(
                it?.toString().orEmpty()
            )
        }


        binding.etObservacao.doAfterTextChanged {

            viewModel.onObservacaoChanged(
                it?.toString().orEmpty()
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding =
            ActivityRegistroBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            val teclado = insets.getInsets(
                WindowInsetsCompat.Type.ime()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, teclado.bottom)
            )

            insets
        }


        fusedLocationClient =
            LocationServices
                .getFusedLocationProviderClient(this)

        configurarSeletorCausas()

        configurarClicks()

        configurarCamposFormulario()

        observarEventos()

        observarFormulario()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mostrarDialogConfirmacao()
            }
        })
    }

    override fun onDestroy() {
        dialogCamera?.dismiss()
        dialogCamera = null
        dialogLocalizacao?.dismiss()
        dialogLocalizacao = null
        super.onDestroy()
    }

    private fun mostrarDialogConfirmacao() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirmar_saida_registro_titulo))
            .setMessage(getString(R.string.confirmar_saida_registro_msg))
            .setPositiveButton(getString(R.string.sim)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.nao), null)
            .show()
    }

    private fun renderizarFormulario(
        state: RegistroFormState
    ) {

        binding.tilData.error =
            state.erroData

        binding.tilObservacao.error =
            state.erroObservacao

        binding.tvErroFoto.visibility =
            if (state.erroFoto)
                View.VISIBLE
            else
                View.GONE

        state.imageUri?.let { uriString ->

            binding.imgPreview.setImageURI(null)

            binding.imgPreview.setImageURI(
                Uri.parse(uriString)
            )
        }

        if (
            binding.etData.text.toString()
            != state.data
        ) {

            binding.etData.setText(state.data)
        }


        val usandoOutro =
            state.suspeitaSelecionada == SuspeitaMorte.OUTRO

        binding.tvErroCausa.text =
            state.erroCausa.orEmpty()

        binding.tvErroCausa.visibility =
            if (state.erroCausa != null && !usandoOutro) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.tilCausaPersonalizada.visibility =
            if (usandoOutro) View.VISIBLE else View.GONE

        binding.tilCausaPersonalizada.error =
            if (usandoOutro) state.erroCausa else null

        if (
            binding.etCausaPersonalizada.text.toString()
            != state.causaPersonalizada
        ) {
            binding.etCausaPersonalizada.setText(
                state.causaPersonalizada
            )
        }

        renderizarSeletorCausas(state)


        if (
            binding.etObservacao.text.toString()
            != state.observacao
        ) {

            binding.etObservacao.setText(
                state.observacao
            )
        }

        if (
            state.latitude != null &&
            state.longitude != null
        ) {

            binding.tvLatitude.text =
                "Latitude: ${state.latitude}"

            binding.tvLongitude.text =
                "Longitude: ${state.longitude}"
        }

        if (state.exibindoDialogCamera) {
            if (dialogCamera == null || dialogCamera?.isShowing == false) {
                exibirDialogOrientacaoCamera()
            }
        } else {
            dialogCamera?.dismiss()
            dialogCamera = null
        }

        if (state.exibindoDialogLocalizacao) {
            if (dialogLocalizacao == null || dialogLocalizacao?.isShowing == false) {
                exibirDialogPermissaoLocalizacao()
            }
        } else {
            dialogLocalizacao?.dismiss()
            dialogLocalizacao = null
        }
    }

    private fun observarFormulario() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.formState.collect { state ->

                    renderizarFormulario(state)
                }
            }
        }
    }

    private fun observarEventos() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.event.collect { event ->

                    when (event) {

                        is RegistroEvent.SalvarRegistro -> {

                            finalizarRegistro(
                                event.registro
                            )
                        }

                        RegistroEvent.AbrirCamera -> {

                            val intent = Intent(
                                this@RegistroActivity,
                                CameraActivity::class.java
                            )
                            Log.d("CameraDebug", "RegistroActivity recebeu AbrirCamera")
                            cameraLauncher.launch(intent)
                        }

                        RegistroEvent.AbrirDatePicker -> {

                            abrirSeletorData()
                        }

                        RegistroEvent.SolicitarPermissaoCamera -> {

                            if (verificarPermissaoCamera()) {

                                viewModel.onPermissaoCameraConcedida()

                            } else {

                                permissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            }
                        }

                        RegistroEvent.SolicitarLocalizacao -> {
                            if (verificarPermissaoLocalizacao()) {
                                capturarLocalizacao()
                            } else {
                                viewModel.solicitarPermissaoLocalizacao()
                            }
                        }

                        RegistroEvent.AbrirPermissaoLocalizacaoSistema -> {
                            locationPermissionLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        }
                    }
                }
            }
        }
    }

    private fun exibirDialogPermissaoLocalizacao() {
        dialogLocalizacao = AlertDialog.Builder(this)
            .setTitle(getString(R.string.permissao_localizacao_titulo))
            .setMessage(getString(R.string.permissao_localizacao_msg))
            .setPositiveButton(getString(R.string.permitir)) { _, _ ->
                viewModel.onConfirmarDialogLocalizacao()
            }
            .setNegativeButton(getString(R.string.agora_nao)) { _, _ ->
                viewModel.onCancelarDialogLocalizacao()
            }
            .setOnCancelListener {
                viewModel.onCancelarDialogLocalizacao()
            }
            .show()
    }

    private fun exibirDialogOrientacaoCamera() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_orientacao_camera, null)

        dialogCamera = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton(getString(R.string.entender_abrir_camera)) { _, _ ->
                viewModel.onConfirmarDialogCamera()
            }
            .setNegativeButton(getString(R.string.agora_nao)) { _, _ ->
                viewModel.onCancelarDialogCamera()
            }
            .setOnCancelListener {
                viewModel.onCancelarDialogCamera()
            }
            .show()
    }

    private fun abrirSeletorData() {

        val calendario = Calendar.getInstance()

        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                val dataSelecionada =
                    Calendar.getInstance()

                dataSelecionada.set(
                    year,
                    month,
                    dayOfMonth
                )

                val formato =
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    )

                viewModel.onDataChanged(
                    formato.format(dataSelecionada.time)
                )
            },
            ano,
            mes,
            dia
        )

        datePickerDialog.show()
    }

    private fun configurarClicks() {

        // Data
        binding.etData.setOnClickListener {

            viewModel.onDataClicked()
        }

        // Botão foto
        binding.btnFoto.setOnClickListener {

            viewModel.onFotoClicked()
        }

        // Botão salvar
        binding.btnSalvar.setOnClickListener {
            viewModel.onSalvarClicked()
        }
    }

    private fun verificarPermissaoCamera():
            Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun verificarPermissaoLocalizacao():
            Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                viewModel.onPermissaoCameraConcedida()
            }
        }

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                capturarLocalizacao()
            }
        }

    private val cameraLauncher =

        registerForActivityResult(

            ActivityResultContracts.StartActivityForResult()

        ) { result ->

            if (
                result.resultCode == RESULT_OK
            ) {

                val caminho =

                    result.data?.getStringExtra(
                        "foto"
                    )

                caminho?.let {

                    viewModel.onFotoCapturada(it)

                }

            }

        }

        private fun capturarLocalizacao() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    viewModel.onLocalizacaoObtida(
                        location.latitude,
                        location.longitude
                    )
                }
            }
    }


    private fun dp(valor: Int): Int {
        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }

    private fun configurarSeletorCausas() {
        binding.containerCategoriasMortalidade.removeAllViews()

        cabecalhosCategoria.clear()
        gruposSuspeitas.clear()
        radiosSuspeitas.clear()

        CategoriaMortalidade.values().forEach { categoria ->

            val cabecalho = TextView(this).apply {
                text = "▸ ${categoria.descricao}"
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setPadding(dp(16), dp(14), dp(16), dp(14))
                setBackgroundResource(R.drawable.bg_localizacao)

                isClickable = true
                isFocusable = true

                contentDescription =
                    "Expandir categoria ${categoria.descricao}"

                setOnClickListener {
                    viewModel.onCategoriaMortalidadeSelecionada(
                        categoria
                    )
                }
            }

            val grupo = RadioGroup(this).apply {
                orientation = RadioGroup.VERTICAL
                visibility = View.GONE

                setPadding(
                    dp(8),
                    dp(4),
                    dp(8),
                    dp(4)
                )
            }

            SuspeitaMorte.values()
                .filter { it.categoria == categoria }
                .forEach { suspeita ->

                    val radio = RadioButton(this).apply {
                        id = View.generateViewId()
                        text = suspeita.descricao
                        textSize = 14f

                        setPadding(
                            dp(4),
                            dp(8),
                            dp(4),
                            dp(8)
                        )

                        setOnClickListener {
                            viewModel.onSuspeitaMorteSelecionada(
                                suspeita
                            )
                        }
                    }

                    grupo.addView(
                        radio,
                        RadioGroup.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    )

                    radiosSuspeitas[suspeita] = radio
                }

            binding.containerCategoriasMortalidade.addView(
                cabecalho,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(4)
                }
            )

            binding.containerCategoriasMortalidade.addView(
                grupo,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(8)
                }
            )

            cabecalhosCategoria[categoria] = cabecalho
            gruposSuspeitas[categoria] = grupo
        }
    }

    private fun renderizarSeletorCausas(
        state: RegistroFormState
    ) {
        cabecalhosCategoria.forEach { (categoria, cabecalho) ->

            val expandida =
                state.categoriaSelecionada == categoria

            cabecalho.text =
                "${if (expandida) "▾" else "▸"} ${categoria.descricao}"

            cabecalho.contentDescription =
                "${if (expandida) "Recolher" else "Expandir"} " +
                        "categoria ${categoria.descricao}"

            gruposSuspeitas[categoria]?.visibility =
                if (expandida) View.VISIBLE else View.GONE
        }

        radiosSuspeitas.forEach { (suspeita, radio) ->
            radio.isChecked =
                state.suspeitaSelecionada == suspeita
        }
    }


}