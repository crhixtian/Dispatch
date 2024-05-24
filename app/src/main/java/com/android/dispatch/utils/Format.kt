package com.android.dispatch.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Format {

    fun Calendar.format(out:String = Constants.DATE_FORMAT_FULL):String{
        return SimpleDateFormat(out, Locale.getDefault()).format(this.time)
    }
}