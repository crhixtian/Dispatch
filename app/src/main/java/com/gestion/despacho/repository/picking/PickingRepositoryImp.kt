package com.gestion.despacho.repository.picking

import android.util.Log
import com.gestion.despacho.data.local.AppDataBase
import com.gestion.despacho.data.remote.Api
import com.gestion.despacho.model.ClsPicking
import com.gestion.despacho.model.ClsPickingDetail
import com.gestion.despacho.model.ClsStevedores
import com.gestion.despacho.model.ObserveMaterialRequest
import com.gestion.despacho.model.PickingDetail
import com.gestion.despacho.model.PickingDto
import com.gestion.despacho.model.PickingLoadRequest
import com.gestion.despacho.model.StatusPickingRequest
import com.gestion.despacho.model.Stevedores
import com.gestion.despacho.model.StevedoresRequest
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.Format.format
import com.gestion.despacho.utils.OperationResult
import com.gestion.despacho.utils.SessionManager
import java.util.Calendar

class PickingRepositoryImp : PickingRepository {

    object Picking {
        val getInfoPickingDB = AppDataBase.instance?.pickingDao()?.getInfoPickingDB()
        val getPickingDB = AppDataBase.instance?.pickingDetailsDao()?.getDetails()
        val getStevedores = AppDataBase.instance?.stevedoresDao()?.getStevedores()
    }

    override suspend fun getPicking(idPicking: String): OperationResult<PickingDto> {
        return try {
            val response = Api.build().getPicking(idPicking)
            if (response.isSuccessful) {
                handleDatabaseOperations(response.body()!!)
            } else {
                OperationResult.Failure(Exception(response.body()?.Description))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    private suspend fun handleDatabaseOperations(pickingDto: PickingDto): OperationResult<PickingDto> {
        return when (AppDataBase.instance?.pickingDao()?.getCountPicking()) {
            1 -> handleSinglePicking(pickingDto)
            0 -> handleEmptyDatabase(pickingDto)
            else -> OperationResult.Failure(Exception("Ocurrió un error obteniendo el número de pickings"))
        }
    }

    private suspend fun handleSinglePicking(pickingDto: PickingDto): OperationResult<PickingDto> {
        return if (cleanDB() > 0) {
            handleSavePicking(pickingDto)
        } else {
            OperationResult.Failure(Exception("Error al limpiar la BD"))
        }
    }

    private suspend fun handleEmptyDatabase(pickingDto: PickingDto): OperationResult<PickingDto> {
        return handleSavePicking(pickingDto)
    }

    private suspend fun handleSavePicking(pickingDto: PickingDto): OperationResult<PickingDto> {
        return if (savePicking(pickingDto.Data) > 0) {
            if (savePickingDetail(pickingDto.Data.pickingDet) > 0) {
                OperationResult.Complete(pickingDto)
            } else {
                OperationResult.Failure(Exception("Error al guardar detalle"))
            }
        } else {
            OperationResult.Failure(Exception("Error al obtener el picking"))
        }
    }

    override suspend fun savePicking(picking: com.gestion.despacho.model.Picking): Int {
        var response: Long = 0
        try {
            SessionManager().saveStatus(picking.status)
            val objPicking = ClsPicking(
                0, picking.nbrpicking, picking.date_deliv, picking.date_mov_goods,
                picking.cod_sap_requ, picking.petitioner, picking.plate, picking.driver, picking.license, picking.Hour, 1, picking.observation
            )

            response = AppDataBase.instance?.pickingDao()?.insert(objPicking)!!

        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
        return response.toInt()
    }

    override suspend fun getInfoPicking(id: String): OperationResult<ClsPicking> {
        return try {
            val response = AppDataBase.instance?.pickingDao()?.getInfoPicking(id)

            if (response != null) {
                OperationResult.Complete(data = response)
            } else {
                OperationResult.Failure(java.lang.Exception(Constants.RESPONSE_ERROR))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun cleanDB(): Int {
        return try {
            val response = AppDataBase.instance?.pickingDao()?.deleteAll()
            if (response!! > 0) {
                1
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun savePickingDetail(p: List<PickingDetail>): Int {
        var response: Long = 0
        try {

            p.forEach {
                val idpicking = AppDataBase.instance?.pickingDao()?.getIdPicking(it.nbrpicking)
                val objPickingDetail = ClsPickingDetail(
                    0,
                    idpicking!!,
                    it.cod_sap_material,
                    it.material,
                    it.quantity,
                    it.weight,
                    it.ton,
                    it.type_load,
                    it.start,
                    it.end,
                    it.nrolote,
                    it.observation
                )

                response = AppDataBase.instance?.pickingDetailsDao()!!.insert(objPickingDetail)

                it.stevedores.forEach { s ->
                    AppDataBase.instance?.stevedoresDao()?.insert(
                        ClsStevedores(
                            0, idpicking,
                            it.cod_sap_material, it.type_load, it.material, s.nombre, s.dni
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("ASCT", e.message.toString())
        }
        return response.toInt()
    }

    override suspend fun checkStevedores(entity: ClsPickingDetail): OperationResult<ClsPickingDetail> {
        return try {
            val response = AppDataBase.instance?.stevedoresDao()
                ?.checkStevedores(entity.IdPicking.toString(), entity.cod_sap_material)

            if (response?.size!! > 0) {
                OperationResult.Complete(entity)
            } else {
                OperationResult.Failure(java.lang.Exception(Constants.ERROR_NO_STEVEDORES))
            }

        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun checkPicking(id: String): OperationResult<Int> {
        return try {
            val idPicking = AppDataBase.instance?.pickingDao()?.getIdPicking(id)
            val materials = idPicking?.let {
                AppDataBase.instance?.pickingDetailsDao()?.checkPicking()
            }
            var flag = true

            materials!!.forEach {
                val start = AppDataBase.instance?.pickingDetailsDao()?.getStartDate(it.Id)
                val end = AppDataBase.instance?.pickingDetailsDao()?.getEndDate(it.Id)

                if (start!!.isEmpty() || end!!.isEmpty()) {
                    flag = false
                }
            }

            if (flag) {
                OperationResult.Complete(1)
                //verifiedPicking(id)
            } else {
                OperationResult.Failure(java.lang.Exception("Falta dar inicio y/o fin a un material"))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun observeMaterialApi(
        material: ClsPickingDetail?,
        motivo: String
    ): OperationResult<Int> {
        return try {
            val picking = AppDataBase.instance?.pickingDao()?.getNbrPicking(material!!.IdPicking)
            val response = Api.build().observeMaterial(
                ObserveMaterialRequest(
                    picking!!,
                    material!!.cod_sap_material,
                    motivo
                )
            )

            if (response.isSuccessful) {
                OperationResult.Complete(response.body()?.Code)
            } else {
                OperationResult.Failure(java.lang.Exception(response.body()?.Description))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun observeMaterial(
        material: ClsPickingDetail?,
        motivo: String
    ): OperationResult<Int> {
        return try {
            material?.observation = motivo
            val response = AppDataBase.instance?.pickingDetailsDao()?.updateDetails(material!!)!!
            if (response > 0) {
                OperationResult.Complete(1)
            } else {
                OperationResult.Failure(java.lang.Exception("Ocurrió un error"))
            }

        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun verifiedPicking(id: String): OperationResult<Int> {
        return try {
            SessionManager().getUsuario().let {
                val obs = AppDataBase.instance?.pickingDao()?.getObservation(id)
                val response = Api.build()
                    .statusPicking(
                        StatusPickingRequest(
                            id,
                            1,
                            obs,
                            SessionManager().getUsuario()!!
                        )
                    )
                if (response.isSuccessful) {
                    if (response.body()?.Code == 200) {
                        OperationResult.Complete(response.body()?.Code)
                    } else {
                        OperationResult.Failure(java.lang.Exception("${response.body()?.Code}"))
                    }
                } else {
                    OperationResult.Failure(java.lang.Exception("${response.body()?.Description}"))
                }
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun observePickingApi(id: String, motivo: String): OperationResult<Int> {
        return try {
            val response = Api.build().statusPicking(
                StatusPickingRequest(
                    id,
                    3,
                    motivo,
                    SessionManager().getUsuario().toString()
                )
            )

            if (response.isSuccessful) {
                if (response.body()?.Code == 200) {
                    OperationResult.Complete(response.body()?.Code)
                } else {
                    OperationResult.Failure(java.lang.Exception(response.body()?.toString()))
                }
            } else {
                OperationResult.Failure(java.lang.Exception(response.body()?.toString()))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun observePicking(id: String, motivo: String): OperationResult<String> {

        return try {
            val idPicking = AppDataBase.instance?.pickingDetailsDao()?.getId(id)

            val response = {
                AppDataBase.instance?.pickingDao()?.updateStatus(3, id)
                if (idPicking != null) {
                    AppDataBase.instance?.pickingDao()?.updateObservation(motivo, id)
                }
            }
            println(response)
            OperationResult.Complete("Picking observado")

        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun startLoad(
        picking: ClsPickingDetail,
        nbrLot: String
    ): OperationResult<String> {
        return try {

            picking.startDate = "${getDate()} ${getHour()}"
            picking.nbr_lot = nbrLot

            val response = AppDataBase.instance?.pickingDetailsDao()?.updateDetails(picking)

            if (response != null) {
                OperationResult.Complete(Constants.RESPONSE_SUCCESSFULLY)
            } else {
                OperationResult.Failure(java.lang.Exception(Constants.RESPONSE_ERROR))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun endLoad(picking: ClsPickingDetail): OperationResult<String> {
        return try {
            picking.endDate = "${getDate()} ${getHour()}"

            val response = AppDataBase.instance?.pickingDetailsDao()?.updateDetails(picking)

            if (response != null) {
                OperationResult.Complete(Constants.RESPONSE_SUCCESSFULLY)
            } else {
                OperationResult.Failure(java.lang.Exception(Constants.RESPONSE_ERROR))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun saveStevedor(stevedor: ClsStevedores): OperationResult<String> {
        return try {
            val response = AppDataBase.instance?.stevedoresDao()?.insert(stevedor)

            if (response != null) {
                OperationResult.Complete(Constants.STEVEDOR_SUCCES)
            } else {
                OperationResult.Failure(java.lang.Exception(Constants.RESPONSE_ERROR))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun updateStevedoresApi(stevedor: ClsStevedores): OperationResult<Int> {
        return try {
            val nbrPicking = AppDataBase.instance?.pickingDao()?.getNbrPicking(stevedor.IdPicking)

            val response = Api.build().updateStevedor(
                StevedoresRequest(
                    nbrPicking!!,
                    stevedor.cod_sap_material,
                    stevedor.type_load,
                    Stevedores(stevedor.nombre, stevedor.dni)
                )
            )

            if (response.isSuccessful) {
                if (response.body()?.Code == 200) {
                    OperationResult.Complete(response.body()?.Code)
                } else {
                    OperationResult.Failure(java.lang.Exception(response.body()?.toString()))
                }
            } else {
                OperationResult.Failure(java.lang.Exception(response.body()?.toString()))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun updateStevedores(stevedor: ClsStevedores): OperationResult<String> {
        return try {

            val response = AppDataBase.instance?.stevedoresDao()?.update(stevedor)

            if (response != null) {
                OperationResult.Complete("Success")
            } else {
                OperationResult.Failure(java.lang.Exception("Ocurrió un error al actualizar el estibador"))
            }

        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun deleteStevedorApi(stevedor: ClsStevedores): OperationResult<Int> {
        return try {
            val nbrPicking = AppDataBase.instance?.pickingDao()?.getNbrPicking(stevedor.IdPicking)
            //val obj= nbrPicking?.let { PickingLoadRequest(it,entity.cod_sap_material, getDate(), 1, getHour(), edtNbrLot, "jorreaga") }

            val response = Api.build().deleteStevedor(
                StevedoresRequest(
                    nbrPicking!!, stevedor.cod_sap_material,
                    stevedor.type_load, Stevedores(stevedor.nombre, stevedor.dni)
                )
            )

            if (response.isSuccessful) {
                if (response.body()?.Code == 200) {
                    OperationResult.Complete(response.body()?.Code)
                } else {
                    OperationResult.Failure(java.lang.Exception("${response.body()?.Description}"))
                }
            } else {
                OperationResult.Failure(java.lang.Exception(response.body()?.Description))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun deleteStevedor(stevedor: ClsStevedores): OperationResult<String> {
        return try {

            val response = AppDataBase.instance?.stevedoresDao()?.delete(stevedor)

            if (response != null) {
                OperationResult.Complete("Eliminado correctamente")
            } else {
                OperationResult.Failure(java.lang.Exception("Ocurrió un error al eliminar el estibador"))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun startLoadApi(
        entity: ClsPickingDetail,
        edtNbrLot: String
    ): OperationResult<Int> {
        return try {
            val nbrPicking = AppDataBase.instance?.pickingDao()?.getNbrPicking(entity.IdPicking)
            val obj = nbrPicking?.let {
                SessionManager().getUsuario()?.let { user ->
                    PickingLoadRequest(
                        it,
                        entity.cod_sap_material,
                        getDate(),
                        1,
                        getHour(),
                        edtNbrLot,
                        user
                    )
                }
            }

            val response = obj?.let { Api.build().loadPicking(it) }

            if (response!!.isSuccessful) {
                if (response.body()?.Code == 200) {
                    //startLoad(entity, edtNbrLot)
                    OperationResult.Complete(response.body()?.Code)
                } else {
                    OperationResult.Failure(java.lang.Exception("${response.body()?.Description}"))
                }
            } else {
                OperationResult.Failure(java.lang.Exception(response.body()?.Description))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun addStevedor(objStevedor: ClsStevedores): OperationResult<Int> {
        return try {

            val nbrPicking =
                AppDataBase.instance?.pickingDao()?.getNbrPicking(objStevedor.IdPicking)
            val obj = nbrPicking?.let {
                StevedoresRequest(
                    it, objStevedor.cod_sap_material, objStevedor.type_load,
                    Stevedores(objStevedor.nombre, objStevedor.dni)
                )
            }
            val response = obj?.let { Api.build().stevedoresPicking(it) }

            if (response!!.isSuccessful) {
                if (response.body()?.Code == 200) {
                    OperationResult.Complete(response.body()?.Code)
                } else {
                    OperationResult.Failure(java.lang.Exception("${response.body()?.Code}"))
                }
            } else {
                OperationResult.Failure(java.lang.Exception(response.body()?.Description))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun endLoadApi(entity: ClsPickingDetail): OperationResult<Int> {
        return try {
            val nbrPicking = AppDataBase.instance?.pickingDao()?.getNbrPicking(entity.IdPicking)
            val obj = nbrPicking?.let {
                SessionManager().getUsuario()?.let { user ->
                    entity.nbr_lot?.let { lote ->
                        PickingLoadRequest(
                            it, entity.cod_sap_material, getDate(),
                            2, getHour(), lote, user
                        )
                    }
                }
            }
            val response = obj?.let { Api.build().loadPicking(it) }

            if (response!!.isSuccessful) {
                if (response.body()?.Code == 200) {
                    OperationResult.Complete(response.body()?.Code)
                } else {
                    OperationResult.Failure(java.lang.Exception("${response.body()?.Description}"))
                }
            } else {
                OperationResult.Failure(java.lang.Exception(response.body()?.Description))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    private fun getDate(): String {
        val now = Calendar.getInstance()
        return now.format(Constants.DATE_FORMAT_FULL)
    }

    private fun getHour(): String {
        val time = Calendar.getInstance()
        return time.format(Constants.HOUR_FORMAT)
    }
}