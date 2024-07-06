package com.gestion.despacho.presentation.picking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gestion.despacho.R
import com.gestion.despacho.databinding.ActivityPickingBinding
import com.gestion.despacho.model.User
import com.gestion.despacho.presentation.infoPicking.InfoPickingFragment
import com.gestion.despacho.presentation.infoUser.InfoUserFragment
import com.gestion.despacho.presentation.materialsPicking.MaterialsPickingFragment
import com.gestion.despacho.presentation.stevedores.StevedoresFragment
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.DialogManager
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class PickingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPickingBinding
    lateinit var id: String
    private lateinit var globalUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundleReception = intent.extras
        bundleReception?.apply {
            id = this.getString(Constants.PICKING).toString()
            globalUser = this.getSerializable(Constants.USER_LOGIN) as User
        }

        validateConnection()
        replaceFragment(InfoPickingFragment())

        bottomNavManager()

        val thread = Thread {
            while (true) {
                validateConnection()
                try {
                    TimeUnit.MILLISECONDS.sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }

    private fun bottomNavManager() {
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.infoPickingFragment -> replaceFragment(InfoPickingFragment())

                R.id.materialsPickingFragment -> replaceFragment(MaterialsPickingFragment())

                R.id.stevedoresFragment -> replaceFragment(StevedoresFragment())

                R.id.InfoUserFragment -> {
                    val bundle = Bundle()
                    bundle.putSerializable(Constants.USER_LOGIN, globalUser)
                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    val myFragment = InfoUserFragment()
                    myFragment.arguments = bundle
                    fragmentTransaction.replace(R.id.frameLayout, myFragment)
                    fragmentTransaction.commit()
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString(Constants.PICKING_FRAGMENT, id)
        val fragmentManager = supportFragmentManager
        fragment.arguments = bundle
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun validateConnection() {
        if (!getConnection()) {
            runOnUiThread {
                DialogManager.connectionDialog(this)
            }
        } else {
            runOnUiThread {
                DialogManager.hideDisconnection()
            }
        }
    }

    private fun getConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}
