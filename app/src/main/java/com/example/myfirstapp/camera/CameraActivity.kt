package com.example.myfirstapp.camera


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.databinding.ActivityCameraBinding
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private val repository = CameraRepository()

    private var cameraFrontal = false

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

        repository.capturarFoto(this) { caminho ->

            val intent = Intent()

            intent.putExtra(
                "foto",
                caminho
            )

            setResult(
                RESULT_OK,
                intent
            )

            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityCameraBinding.inflate(layoutInflater)

        setContentView(binding.root)

        repository.iniciarCamera(
            this,
            this,
            binding.viewFinder,
            false
        )

        configurarClicks()
    }

    private fun configurarClicks() {

        binding.btnCapturar.setOnClickListener {

            capturarFoto()

        }

        binding.btnTrocarCamera.setOnClickListener {

            trocarCamera()

        }

        binding.btnVoltar.setOnClickListener {

            finish()

        }
    }

}