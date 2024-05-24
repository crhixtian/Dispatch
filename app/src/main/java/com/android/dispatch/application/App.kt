package com.android.dispatch.application

import android.app.Application
import android.content.Context
import com.android.dispatch.data.local.AppDataBase
import com.android.dispatch.utils.SessionManager

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