package com.gestion.despacho.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gestion.despacho.model.ClsPicking
import com.gestion.despacho.model.ClsPickingDetail
import com.gestion.despacho.model.ClsStevedores
import com.gestion.despacho.data.daos.PickingDao
import com.gestion.despacho.data.daos.PickingDetailDao
import com.gestion.despacho.data.daos.StevedoresDao

@Database(entities = [ClsPicking::class, ClsPickingDetail::class, ClsStevedores::class], version = 1, exportSchema = true)
abstract class AppDataBase: RoomDatabase() {

    //Def daos
    abstract fun pickingDao(): PickingDao
    abstract fun pickingDetailsDao(): PickingDetailDao
    abstract fun stevedoresDao(): StevedoresDao


    companion object{

        var instance: AppDataBase? = null

        fun getInstanceDB(context: Context): AppDataBase {
            if (instance == null){
                //Create
                instance = Room.databaseBuilder(
                    context, AppDataBase::class.java, "BDdespacho").build()
            }
            return instance as AppDataBase
        }
    }
}