package com.gestion.gestionmantenimientosoftware.Utils

import java.text.SimpleDateFormat
import java.util.*

object Format {

    fun Calendar.format(out:String = Constants.DATE_FORMAT_FULL):String{
        return SimpleDateFormat(out, Locale.getDefault()).format(this.time)
    }
}