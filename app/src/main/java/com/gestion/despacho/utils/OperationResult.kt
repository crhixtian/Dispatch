package com.gestion.despacho.utils

sealed class OperationResult <out T>{

    data class Complete<T>(val data: T?) : OperationResult <T>()
    data class Failure(val exception: Exception?) : OperationResult <Nothing>()
}
