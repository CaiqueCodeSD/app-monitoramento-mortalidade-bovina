package com.example.myfirstapp.camera


import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File


class CameraRepository {


    private var cameraProvider:
            ProcessCameraProvider? = null


    private var imageCapture:
            ImageCapture? = null

    fun capturarFoto(
        context: Context,
        callback: (String) -> Unit
    ) {


        val arquivo = File(

            context.filesDir,

            "registro_${System.currentTimeMillis()}.jpg"

        )


        val outputOptions =

            ImageCapture.OutputFileOptions
                .Builder(arquivo)
                .build()



        imageCapture?.let { capture ->


            capture.takePicture(

                outputOptions,

                ContextCompat.getMainExecutor(context),


                object :
                    ImageCapture.OnImageSavedCallback {


                    override fun onImageSaved(
                        output:
                        ImageCapture.OutputFileResults
                    ) {

                        callback(
                            arquivo.absolutePath
                        )

                    }


                    override fun onError(
                        exception:
                        ImageCaptureException
                    ) {

                    }

                }

            )

        }
    }

    fun iniciarCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        cameraFrontal: Boolean
    ) {


        val providerFuture =
            ProcessCameraProvider.getInstance(
                context
            )



        providerFuture.addListener({


            cameraProvider =
                providerFuture.get()



            val cameraSelector =

                if(cameraFrontal)

                    CameraSelector.DEFAULT_FRONT_CAMERA

                else

                    CameraSelector.DEFAULT_BACK_CAMERA



            val preview = Preview.Builder()
                .build()

            imageCapture =
                ImageCapture.Builder()
                    .build()

            preview.setSurfaceProvider(
                previewView.surfaceProvider
            )



            cameraProvider?.unbindAll()



            cameraProvider?.bindToLifecycle(

                lifecycleOwner,

                cameraSelector,

                preview,

                imageCapture!!

            )



        },

            ContextCompat.getMainExecutor(context))

    }

}