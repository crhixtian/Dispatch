package com.gestion.despacho

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gestion.despacho.utils.DialogManager
import com.gestion.despacho.utils.Toast.Toast
import com.gestion.despacho.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkPermission()){
            //this.Toast("Permiso aceptado")
        }else{
            requestPermissions()
        }

        val thread = Thread {
            while (true){
                validateConnection()
                try{
                    TimeUnit.MILLISECONDS.sleep(2000)
                }catch (e: InterruptedException){
                    e.printStackTrace()
                }
            }
        }

        thread.start()
    }

    private fun validateConnection(){
        if (!getConnection()) {
            runOnUiThread {
                DialogManager.connectionDialog(this)
            }
        }else{
            runOnUiThread {
                DialogManager.hideDisconnection()
            }
        }
    }

    private fun getConnection(): Boolean {
        val conn: ConnectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = conn.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

    private fun disconnectionDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("SIN CONEXIÓN A INTERNET")
        builder.setMessage("Verifique su conexión a internet")
        builder.setCancelable(false)

        return builder.create()
    }

    private fun checkPermission(): Boolean{
        val permission1 = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permission2 = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            200
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 200){
            if (grantResults.isNotEmpty()){
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if(writeStorage && readStorage){
                    this.Toast("Permiso concedido")
                }else{
                    //this.Toast("Permiso denegado")
                    //requestPermissions()
                    //finish()
                }
            }
        }
    }
}