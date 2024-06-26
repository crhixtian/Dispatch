@file:Suppress("DEPRECATION")

package com.android.dispatch

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.dispatch.databinding.ActivityMainBinding
import com.android.dispatch.utils.DialogManager
import com.android.dispatch.utils.Toast.Toast
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkPermission()){
            this.Toast("Permission accept")
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
            arrayOf(
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
                    this.Toast("Permission granted")
                }else{
                    this.Toast("Permission denied")
                }
            }
        }
    }
}