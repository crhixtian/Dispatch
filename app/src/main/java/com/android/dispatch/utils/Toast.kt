package com.android.dispatch.utils

import android.content.Context
import android.widget.Toast

object Toast {

    fun Context.Toast(Mensaje: String){
        Toast.makeText(this,Mensaje, Toast.LENGTH_SHORT).show()
    }
}