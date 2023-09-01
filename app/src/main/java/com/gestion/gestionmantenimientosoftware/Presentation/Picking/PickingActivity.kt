package com.gestion.gestionmantenimientosoftware.Presentation.Picking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gestion.gestionmantenimientosoftware.Model.User
import com.gestion.gestionmantenimientosoftware.Presentation.InfoPicking.InfoPickingFragment
import com.gestion.gestionmantenimientosoftware.Presentation.InfoUser.InfoUserFragment
import com.gestion.gestionmantenimientosoftware.Presentation.MaterialsPicking.MaterialsPickingFragment
import com.gestion.gestionmantenimientosoftware.Presentation.Stevedores.StevedoresFragment
import com.gestion.gestionmantenimientosoftware.R
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.DialogManager
import com.gestion.gestionmantenimientosoftware.databinding.ActivityPickingBinding
import java.util.concurrent.TimeUnit

class PickingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPickingBinding
    lateinit var id: String
    lateinit var globalUser: User

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

    private fun bottomNavManager() {
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId){
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

                else ->{

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val bundle = Bundle()
        bundle.putString(Constants.PICKING_FRAGMENT, id)
        val fragmentManager = supportFragmentManager
        fragment.arguments = bundle
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
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
}