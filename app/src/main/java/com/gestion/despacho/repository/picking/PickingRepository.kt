package com.gestion.gestionmantenimientosoftware.Repository.Picking

import com.gestion.gestionmantenimientosoftware.Model.*
import com.gestion.gestionmantenimientosoftware.Utils.OperationResult

interface PickingRepository {

    suspend fun getPicking(idPicking: String): OperationResult<PickingDto>
    suspend fun savePicking(picking: Picking): Int
    suspend fun getInfoPicking(id: String): OperationResult<ClsPicking>
    suspend fun cleanDB(): Int
    suspend fun savePickingDetail(p: List<PickingDetail>): Int
    //suspend fun getDetail(id:String): OperationResult<LiveData<List<ClsPickingDetail>>>
    suspend fun checkStevedores(entity: ClsPickingDetail): OperationResult<ClsPickingDetail>
    suspend fun verifiedPicking(id: String): OperationResult<Int>
    suspend fun observePickingApi(id: String, motivo:String):OperationResult<Int>
    suspend fun observePicking(id: String, motivo: String): OperationResult<String>
    suspend fun startLoad(picking: ClsPickingDetail, nbrLot: String): OperationResult<String>
    suspend fun endLoad(picking: ClsPickingDetail): OperationResult<String>
    suspend fun saveStevedor(stevedor: ClsStevedores): OperationResult<String>
    //suspend fun getStevedores(id: String): OperationResult<LiveData<List<ClsStevedores>>>
    suspend fun updateStevedoresApi(stevedor: ClsStevedores): OperationResult<Int>
    suspend fun updateStevedores(stevedor: ClsStevedores): OperationResult<String>
    suspend fun deleteStevedorApi(stevedor: ClsStevedores): OperationResult<Int>
    suspend fun deleteStevedor(stevedor: ClsStevedores): OperationResult<String>
    suspend fun startLoadApi(entity: ClsPickingDetail, edtNbrLot: String): OperationResult<Int>
    suspend fun addStevedor(objStevedor: ClsStevedores): OperationResult<Int>
    suspend fun endLoadApi(entity: ClsPickingDetail): OperationResult<Int>
    suspend fun checkPicking(id: String): OperationResult<Int>
    suspend fun observeMaterialApi(material: ClsPickingDetail?, motivo: String): OperationResult<Int>
    suspend fun observeMaterial(material: ClsPickingDetail?, motivo: String): OperationResult<Int>

}