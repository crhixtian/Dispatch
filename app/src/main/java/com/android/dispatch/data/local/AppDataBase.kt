package com.android.dispatch.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.dispatch.data.daos.PickingDao
import com.android.dispatch.data.daos.PickingDetailDao
import com.android.dispatch.data.daos.StevedoresDao
import com.android.dispatch.model.ClsPicking
import com.android.dispatch.model.ClsPickingDetail
import com.android.dispatch.model.ClsStevedores

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