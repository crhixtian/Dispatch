package com.gestion.despacho.utils

import android.content.Context
import android.content.SharedPreferences
import com.gestion.despacho.model.User

class SessionManager {

    companion object{
        private var prefs: SharedPreferences? = null

        fun init(context: Context){
            prefs = context.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE)
        }
    }
    fun saveUrl(baseUrl: String){
        prefs?.edit()?.apply{
            putString(Constants.PREFS_API, baseUrl)
            apply()
        }
    }
    fun getBaseUrl(): String? {
        return prefs?.getString(Constants.PREFS_API, "http://190.119.170.245:85/")
    }
    fun saveStatus(status: Int){
        prefs?.edit()?.apply {
            putInt(Constants.PREFS_STATUS, status)
            apply()
        }
    }
    fun getStatus(): Int?{
        return prefs?.getInt(Constants.PREFS_STATUS, 0)
    }
    fun saveStatuSession(status: Boolean){
        prefs?.edit()?.apply {
            putBoolean(Constants.STATUS_SESSION, status)
            apply()
        }
    }
    fun getStatusSession(): Boolean? {
        return prefs?.getBoolean(Constants.STATUS_SESSION, false)
    }
    fun saveUser(user: String){
        prefs?.edit()?.apply {
            putString(Constants.USER, user)
            apply()
        }
    }
    fun getUser(): String?{
        return prefs?.getString(Constants.USER, "")
    }
    fun saveRolId(rolId: Int){
        prefs?.edit()?.apply {
            putInt(Constants.ROL, rolId)
            apply()
        }
    }
    fun getRolId(): Int?{
        return prefs?.getInt(Constants.ROL, 0)
    }

    fun saveDateVerify(date: String){
        prefs?.edit()?.apply {
            putString(Constants.DATE_VERIFY, date)
            apply()
        }
    }

    fun getDateVerify(): String?{
        return prefs?.getString(Constants.DATE_VERIFY, "")
    }

    fun saveHourVerify(hour: String){
        prefs?.edit()?.apply {
            putString(Constants.HOUR_VERIFY, hour)
            apply()
        }
    }

    fun getHourVerify(): String?{
        return prefs?.getString(Constants.HOUR_VERIFY, "")
    }

    fun saveMails(mails: String){
        prefs?.edit()?.apply {
            putString(Constants.MAILS, mails)
            apply()
        }
    }

    fun getMails(): String?{
        return prefs?.getString(Constants.MAILS, "")
    }

    fun saveObjUser(user: User){
        prefs?.edit()?.apply{
            putString("NOMBRE", user.FullName)
            putString("USUARIO", user.User)
            putString("PERFIL", user.RolName)
            putInt("ROL_ID", user.RolId)
            putString("ROL_CODE", user.RolCode)
            apply()
        }
    }

    fun getNameUser():String? {
        return prefs?.getString("NOMBRE", "SIN NOMBRE")
    }

    fun getUsuario(): String?{
        return prefs?.getString("USUARIO", "SIN NOMBRE DE USUARIO")
    }

    fun getPerfil(): String?{
        return prefs?.getString("PERFIL", "SIN PERFIL")
    }

    fun getRolCode(): String? {
        return prefs?.getString("ROL_CODE", "")
    }

    fun getRolIdLogin(): Int?{
        return prefs?.getInt("ROL_ID", 0)
    }

}