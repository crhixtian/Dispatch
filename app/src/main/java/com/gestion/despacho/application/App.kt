package com.gestion.gestionmantenimientosoftware.Application

import android.app.Application
import android.content.Context
import com.gestion.gestionmantenimientosoftware.Data.Local.AppDataBase
import com.gestion.gestionmantenimientosoftware.Utils.SessionManager

class App: Application() {

    //Instance DB
    lateinit var database: AppDataBase

    companion object{
        private var instance: App? = null

        fun context() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        database = AppDataBase.getInstanceDB(this)
        SessionManager.init(this)
    }
}