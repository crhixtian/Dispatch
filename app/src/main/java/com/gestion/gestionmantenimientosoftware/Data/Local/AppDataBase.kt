package com.gestion.gestionmantenimientosoftware.Data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gestion.gestionmantenimientosoftware.Data.Daos.PickingDao
import com.gestion.gestionmantenimientosoftware.Data.Daos.PickingDetailDao
import com.gestion.gestionmantenimientosoftware.Data.Daos.StevedoresDao
import com.gestion.gestionmantenimientosoftware.Model.ClsPicking
import com.gestion.gestionmantenimientosoftware.Model.ClsPickingDetail
import com.gestion.gestionmantenimientosoftware.Model.ClsStevedores

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