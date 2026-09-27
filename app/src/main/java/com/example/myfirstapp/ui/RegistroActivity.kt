package com.example.myfirstapp.ui

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

class RegistroActivity : AppCompatActivity() {

    private val viewModel: RegistroViewModel by viewModel()

    private lateinit var binding: ActivityRegistroBinding

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient

    private fun finalizarRegistro(
        registro: Registro
    ) {

        val resultIntent = Intent()

        resultIntent.putExtra(
            "novo_registro",
            registro
        )

        setResult(
            Activity.RESULT_OK,
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

        binding.etCausa.doAfterTextChanged {

            viewModel.onCausaChanged(
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
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient =
            LocationServices
                .getFusedLocationProviderClient(this)

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

        binding.tilCausa.error =
            state.erroCausa

        binding.tilObservacao.error =
            state.erroObservacao

        binding.tvErroFoto.visibility =
            if (state.erroFoto)
                View.VISIBLE
            else
                View.GONE

        binding.tvErroLocalizacao.visibility =
            if (state.erroLocalizacao)
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

        if (
            binding.etCausa.text.toString()
            != state.causa
        ) {

            binding.etCausa.setText(state.causa)
        }

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
                                locationPermissionLauncher.launch(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            }
                        }
                    }
                }
            }
        }
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

}