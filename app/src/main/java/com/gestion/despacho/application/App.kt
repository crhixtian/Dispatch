package com.gestion.despacho.application

import android.app.Application
import android.content.Context
import com.gestion.despacho.data.local.AppDataBase
import com.gestion.despacho.utils.SessionManager

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