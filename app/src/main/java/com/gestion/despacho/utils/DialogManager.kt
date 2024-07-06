package com.gestion.gestionmantenimientosoftware.Utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.gestion.gestionmantenimientosoftware.R

object DialogManager {

    private var progressDialog: Dialog? = null
    private var connectionDialog: Dialog? = null


    fun showProgress(context: Context){
        if(progressDialog != null ) return
        progressDialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(R.layout.dialog_progress)
            show()
        }
    }

    fun hideProgress(){
        progressDialog?.dismiss()
        //progressDialog?.hide()
        progressDialog = null
    }

    fun connectionDialog(context: Context){
        if(connectionDialog != null ) return
        connectionDialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            //window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(R.layout.dialog_connection)
            show()
        }
    }

    fun hideDisconnection(){
        connectionDialog?.dismiss()
        //connectionDialog?.hide()
        connectionDialog = null
    }
}