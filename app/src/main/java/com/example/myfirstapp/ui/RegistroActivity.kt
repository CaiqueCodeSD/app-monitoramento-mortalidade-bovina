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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myfirstapp.databinding.ActivityRegistroBinding
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.viewmodel.RegistroViewModel
import com.example.myfirstapp.viewmodel.ValidacaoFormularioState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroActivity : AppCompatActivity() {

    private val viewModel: RegistroViewModel by viewModel()

    private lateinit var binding: ActivityRegistroBinding

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient

    private var imageUri: Uri? = null

    private var latitude: Double? = null
    private var longitude: Double? = null

    companion object {

        private const val KEY_IMAGE_URI =
            "key_image_uri"

        private const val KEY_DATA =
            "key_data"

        private const val KEY_CAUSA =
            "key_causa"

        private const val KEY_OBSERVACAO =
            "key_observacao"

        private const val KEY_LATITUDE =
            "key_latitude"

        private const val KEY_LONGITUDE =
            "key_longitude"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityRegistroBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        fusedLocationClient =
            LocationServices
                .getFusedLocationProviderClient(this)

        restaurarEstado(savedInstanceState)

        configurarClicks()
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

                binding.etData.setText(
                    formato.format(
                        dataSelecionada.time
                    )
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

            abrirSeletorData()
        }

        // Botão foto
        binding.btnFoto.setOnClickListener {

            if (verificarPermissaoCamera()) {

                abrirCamera()

            } else {

                permissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }

        // Botão salvar
        binding.btnSalvar.setOnClickListener {

            val validacao =
                viewModel.validarFormulario(
                    data = binding.etData.text.toString(),
                    causa = binding.etCausa.text.toString(),
                    observacao =
                        binding.etObservacao.text.toString(),
                    fotoUri = imageUri?.toString(),
                    latitude = latitude,
                    longitude = longitude
                )

            when (validacao) {

                is ValidacaoFormularioState.Valido -> {

                    val registro = Registro(
                        id = System.currentTimeMillis().toInt(),
                        data = binding.etData.text.toString(),
                        causa = binding.etCausa.text.toString(),
                        observacao =
                            binding.etObservacao.text.toString(),
                        fotoUri = imageUri.toString(),
                        latitude = latitude!!,
                        longitude = longitude!!
                    )

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

                is ValidacaoFormularioState.Invalido -> {

                    binding.tilData.error =
                        validacao.erroData

                    binding.tilCausa.error =
                        validacao.erroCausa

                    binding.tilObservacao.error =
                        validacao.erroObservacao

                    binding.tvErroFoto.visibility =
                        if (validacao.erroFoto)
                            View.VISIBLE
                        else
                            View.GONE

                    binding.tvErroLocalizacao.visibility =
                        if (validacao.erroLocalizacao)
                            View.VISIBLE
                        else
                            View.GONE
                }
            }
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

                abrirCamera()
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
            ActivityResultContracts.TakePicture()
        ) { success ->

            if (success) {

                imageUri?.let {

                    binding.imgPreview.setImageURI(null)

                    binding.imgPreview.setImageURI(it)
                }

                if (verificarPermissaoLocalizacao()) {

                    capturarLocalizacao()

                } else {

                    locationPermissionLauncher.launch(
                        Manifest.permission
                            .ACCESS_FINE_LOCATION
                    )
                }
            }
        }

    private fun abrirCamera() {

        val imageFile = File.createTempFile(
            "registro_",
            ".jpg",
            cacheDir
        )

        imageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            imageFile
        )

        cameraLauncher.launch(imageUri!!)
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

                    latitude = location.latitude
                    longitude = location.longitude

                    binding.tvLatitude.text =
                        "Latitude: $latitude"

                    binding.tvLongitude.text =
                        "Longitude: $longitude"
                }
            }
    }

    override fun onSaveInstanceState(
        outState: Bundle
    ) {
        super.onSaveInstanceState(outState)

        outState.putString(
            KEY_IMAGE_URI,
            imageUri?.toString()
        )

        outState.putString(
            KEY_DATA,
            binding.etData.text.toString()
        )

        outState.putString(
            KEY_CAUSA,
            binding.etCausa.text.toString()
        )

        outState.putString(
            KEY_OBSERVACAO,
            binding.etObservacao.text.toString()
        )

        latitude?.let {
            outState.putDouble(
                KEY_LATITUDE,
                it
            )
        }

        longitude?.let {
            outState.putDouble(
                KEY_LONGITUDE,
                it
            )
        }

    }

    private fun restaurarEstado(
        savedInstanceState: Bundle?
    ) {

        savedInstanceState ?: return

        binding.etData.setText(
            savedInstanceState.getString(KEY_DATA)
        )

        binding.etCausa.setText(
            savedInstanceState.getString(KEY_CAUSA)
        )

        binding.etObservacao.setText(
            savedInstanceState.getString(
                KEY_OBSERVACAO
            )
        )

        val savedImageUri =
            savedInstanceState.getString(
                KEY_IMAGE_URI
            )

        if (savedImageUri != null) {

            imageUri = Uri.parse(savedImageUri)

            binding.imgPreview.setImageURI(null)

            binding.imgPreview.setImageURI(imageUri)
        }

        if (
            savedInstanceState.containsKey(KEY_LATITUDE)
            &&
            savedInstanceState.containsKey(KEY_LONGITUDE)
        ) {

            latitude =
                savedInstanceState.getDouble(KEY_LATITUDE)

            longitude =
                savedInstanceState.getDouble(KEY_LONGITUDE)

            binding.tvLatitude.text =
                "Latitude: $latitude"

            binding.tvLongitude.text =
                "Longitude: $longitude"
        }
    }
}