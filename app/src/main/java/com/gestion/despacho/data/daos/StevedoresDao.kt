package com.gestion.gestionmantenimientosoftware.Data.Daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gestion.gestionmantenimientosoftware.Model.ClsStevedores

@Dao
interface StevedoresDao {

    @Insert
    fun insert(stevedor: ClsStevedores): Long

    @Query("SELECT * FROM Stevedores")
    fun getStevedores(): LiveData<List<ClsStevedores>>

    @Query("SELECT * FROM Stevedores WHERE IdPicking=:Id AND cod_sap_material=:cod_sap_material")
    fun checkStevedores(Id: String, cod_sap_material: String): List<ClsStevedores>
    @Update
    fun update(stevedores: ClsStevedores)

    @Delete
    fun delete(stevedor: ClsStevedores)
}