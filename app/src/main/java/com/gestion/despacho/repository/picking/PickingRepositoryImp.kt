package com.gestion.gestionmantenimientosoftware.Repository.Picking

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.gestion.gestionmantenimientosoftware.Data.Local.AppDataBase
import com.gestion.gestionmantenimientosoftware.Data.Remote.Api
import com.gestion.gestionmantenimientosoftware.Model.*
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.Format.format
import com.gestion.gestionmantenimientosoftware.Utils.OperationResult
import com.gestion.gestionmantenimientosoftware.Utils.SessionManager
import java.util.*
import kotlin.Exception

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
                when(AppDataBase.instance?.pickingDao()?.getCountPicking()){
                    1 -> {
                        if (cleanDB() > 0) {
                            if (savePicking(response.body()!!.Data) > 0) {
                                if (savePickingDetail(response.body()!!.Data.pickingDet) > 0) {
                                    OperationResult.Complete(response.body())
                                } else {
                                    OperationResult.Failure(java.lang.Exception("Error al guarda detalle"))
                                }
                            } else {
                                OperationResult.Failure(java.lang.Exception("Error al obtener el picking"))
                            }
                        } else {
                            OperationResult.Failure(java.lang.Exception("Error al limpiar la BD"))
                        }
                    }
                    0 -> {
                        if (savePicking(response.body()!!.Data) > 0) {
                            if (savePickingDetail(response.body()!!.Data.pickingDet) > 0) {
                                OperationResult.Complete(response.body())
                            } else {
                                OperationResult.Failure(java.lang.Exception("Error al guarda detalle"))
                            }
                        } else {
                            OperationResult.Failure(java.lang.Exception("Error al obtener el picking"))
                        }
                    }

                    else -> {OperationResult.Failure(java.lang.Exception("Ocurrió un error obteniendo el número de pickings"))}
                }
            } else {
                OperationResult.Failure(java.lang.Exception(response.body()?.Description))
            }
        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    override suspend fun savePicking(p: com.gestion.gestionmantenimientosoftware.Model.Picking): Int {
        var response: Long = 0
        try {
            SessionManager().saveStatus(p.status)
            val objPicking = ClsPicking(
                0, p.nbrpicking, p.date_deliv, p.date_mov_goods,
                p.cod_sap_requ, p.petitioner, p.plate, p.driver, p.license, p.Hour, 1, p.observation
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
                    .statusPicking(StatusPickingRequest(id, 1, obs, SessionManager().getUsuario()!!))
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
            if (response != null) {
                OperationResult.Complete("Picking observado")
            } else {
                OperationResult.Failure(java.lang.Exception("Ocurrió un error"))
            }

        } catch (e: Exception) {
            OperationResult.Failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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
            /*val list = mutableListOf<Stevedores>()
            list.add(Stevedores(objStevedor.nombre, objStevedor.dni))*/

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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHour(): String {
        val time = Calendar.getInstance()
        return time.format(Constants.HOUR_FORMAT)
    }
}