package com.gestion.despacho.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gestion.despacho.model.ClsPicking

@Dao
interface PickingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(picking: ClsPicking): Long

    @Query("SELECT count() FROM PickingHeader")
    fun getCountPicking(): Int

    @Query("SELECT * FROM PickingHeader")
    fun getInfoPickingDB(): LiveData<ClsPicking>
    @Query("SELECT * FROM PickingHeader WHERE Picking=:NbrPicking")
    fun getInfoPicking(NbrPicking: String): ClsPicking

    @Query("SELECT Id FROM PickingHeader WHERE Picking=:nbrpicking")
    fun getIdPicking(nbrpicking: String): Int

    @Query("SELECT Picking FROM PickingHeader WHERE Id=:Id")
    fun getNbrPicking(Id: Int): String

    @Query("SELECT observation FROM PickingHeader WHERE Picking=:Id")
    fun getObservation(Id: String): String
    @Query("DELETE FROM PickingHeader")
    fun deleteAll(): Int

    @Query("UPDATE PickingHeader SET Status =:Status WHERE Picking=:Id")
    fun updateStatus(Status: Int, Id: String)

    @Query("UPDATE PickingHeader SET observation =:Obs WHERE Picking=:Id")
    fun updateObservation(Obs: String, Id: String)
}