package com.example.myfirstapp.camera


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.databinding.ActivityCameraBinding
import android.content.Intent
import android.view.ScaleGestureDetector
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.ImageCapture
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myfirstapp.R
import kotlinx.coroutines.launch
import android.widget.Toast
import android.util.Log

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private val repository = CameraRepository()

    private var cameraFrontal = false

    private var capturaEmAndamento = false

    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private fun trocarCamera() {

        cameraFrontal = !cameraFrontal

        repository.iniciarCamera(
            this,
            this,
            binding.viewFinder,
            cameraFrontal
        )
    }


    private fun capturarFoto() {

        // Evita iniciar outra captura enquanto a atual está processando.
        if (capturaEmAndamento) return

        capturaEmAndamento = true
        binding.btnCapturar.isEnabled = false

        repository.capturarFoto(this) { caminho ->

            if (caminho.isNullOrBlank()) {

                // A captura falhou: permite tentar novamente.
                capturaEmAndamento = false
                binding.btnCapturar.isEnabled = true

                Toast.makeText(
                    this,
                    "Não foi possível capturar a foto. Tente novamente.",
                    Toast.LENGTH_LONG
                ).show()

            } else {

                val intent = Intent().apply {
                    putExtra("foto", caminho)
                }

                Log.d("CameraDebug", "Foto capturada; finalizando câmera")

                setResult(
                    RESULT_OK,
                    intent
                )

                finish()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(
            "CameraDebug",
            "CameraActivity onCreate: ${System.identityHashCode(this)}"
        )

        enableEdgeToEdge()

        binding =
            ActivityCameraBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        repository.iniciarCamera(
            this,
            this,
            binding.viewFinder,
            false
        )

        configurarZoom()
        configurarClicks()
    }

    override fun onDestroy() {
        Log.d(
            "CameraDebug",
            "CameraActivity onDestroy: " +
                    "finishing=$isFinishing, " +
                    "changingConfig=$isChangingConfigurations, " +
                    "instance=${System.identityHashCode(this)}"
        )
        super.onDestroy()
    }

    private fun configurarZoom() {
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = repository.getZoomState()?.value?.zoomRatio ?: 1f
                val delta = detector.scaleFactor
                repository.setZoom(currentZoomRatio * delta)
                return true
            }
        })

        binding.viewFinder.setOnTouchListener { view, event ->
            scaleGestureDetector.onTouchEvent(event)
            view.performClick()
            true
        }
    }

    private fun mostrarDialogConfirmacao() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirmar_saida_titulo))
            .setMessage(getString(R.string.confirmar_saida_msg))
            .setPositiveButton(getString(R.string.sim)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.nao), null)
            .show()
    }

    private fun atualizarIconeFlash(modo: Int) {
        val corPrimaria = ContextCompat.getColor(this, R.color.primaryColor)
        val corBranca = ContextCompat.getColor(this, android.R.color.white)

        when (modo) {
            ImageCapture.FLASH_MODE_ON -> {
                binding.btnFlash.backgroundTintList = android.content.res.ColorStateList.valueOf(corBranca)
                binding.btnFlash.imageTintList = android.content.res.ColorStateList.valueOf(corPrimaria)
            }
            else -> {
                binding.btnFlash.backgroundTintList = android.content.res.ColorStateList.valueOf(corPrimaria)
                binding.btnFlash.imageTintList = android.content.res.ColorStateList.valueOf(corBranca)
            }
        }
    }

    private fun configurarClicks() {

        binding.btnCapturar.setOnClickListener {
            Log.d("CameraDebug", "Botão de captura pressionado")
            capturarFoto()
        }

        binding.btnTrocarCamera.setOnClickListener {

            trocarCamera()

        }

        binding.btnFlash.setOnClickListener {
            val novoModo = repository.alternarFlash()
            atualizarIconeFlash(novoModo)
        }

        binding.btnVoltar.setOnClickListener {

            mostrarDialogConfirmacao()

        }
    }

}