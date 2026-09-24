package com.example.myfirstapp.camera

sealed class CameraEvent {

    object IniciarCamera : CameraEvent()

    object CapturarFoto : CameraEvent()

    object TrocarCamera : CameraEvent()

    object FecharCamera : CameraEvent()
}



data class CameraState(

    val cameraFrontal: Boolean = false,

    val fotoCapturada: String? = null

)


