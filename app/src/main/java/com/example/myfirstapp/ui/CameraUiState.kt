package com.example.myfirstapp.ui


data class CameraUiState(

    val cameraAberta: Boolean = false,

    val usandoCameraFrontal: Boolean = false,

    val fotoCapturada: String? = null,

    val erro: String? = null

)