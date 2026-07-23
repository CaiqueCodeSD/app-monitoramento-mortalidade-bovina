package com.example.myfirstapp.camera

sealed class CameraIntent {


    object AbrirCamera : CameraIntent()


    object TrocarCamera : CameraIntent()


    object CapturarFoto : CameraIntent()


    object Voltar : CameraIntent()

}