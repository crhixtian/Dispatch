package com.gestion.gestionmantenimientosoftware.Data.Daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gestion.gestionmantenimientosoftware.Model.ClsPickingDetail

@Dao
interface PickingDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pickingDetail: ClsPickingDetail): Long

    @Query("SELECT * FROM PickingDetail")
    //fun getDetails(Id: Int): List<ClsPickingDetail>
    fun getDetails(): LiveData<List<ClsPickingDetail>>

    @Query("SELECT * FROM PickingDetail")
    fun checkPicking(): List<ClsPickingDetail>

    @Update
    fun updateDetails(pickingDetail: ClsPickingDetail): Int

    @Query("SELECT Id FROM PickingHeader WHERE Picking=:picking")
    fun getId(picking: String): Int

    @Query("UPDATE PickingDetail SET observation=:obs WHERE IdPicking=:Id")
    fun updateObservation(obs: String, Id: Int)

    @Query("SELECT startDate FROM PickingDetail WHERE Id=:Id")
    fun getStartDate(Id: Int): String

    @Query("SELECT endDate FROM PickingDetail WHERE Id=:Id")
    fun getEndDate(Id: Int): String

}