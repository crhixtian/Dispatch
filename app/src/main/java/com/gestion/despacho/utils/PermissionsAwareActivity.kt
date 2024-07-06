package com.gestion.despacho.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gestion.despacho.utils.Toast.Toast

object PermissionsAwareActivity: AppCompatActivity() {

    private val WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST_CODE: Int = 444

    fun Context.VerifyWriteExternalStorage(){
        val permissionWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionWriteExternalStorage == PackageManager.PERMISSION_GRANTED){
            this.Toast("Permiso de almacenamiento concedido")
        }else{
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST_CODE);
        }
    }

    //fun Context.Verify
}