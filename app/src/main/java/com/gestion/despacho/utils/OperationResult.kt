package com.gestion.gestionmantenimientosoftware.Utils

sealed class OperationResult <out T>{

    data class Complete<T>(val data: T?) : OperationResult <T>()
    data class Failure(val exception: Exception?) : OperationResult <Nothing>()
}
