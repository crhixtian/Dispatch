package com.gestion.despacho.presentation.materialsPicking

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestion.despacho.R
import com.gestion.despacho.model.ClsPickingDetail
import com.gestion.despacho.model.ClsStevedores
import com.gestion.despacho.model.Picking
import com.gestion.despacho.repository.mail.MailRepository
import com.gestion.despacho.repository.picking.PickingRepository
import com.gestion.despacho.repository.picking.PickingRepositoryImp
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.Format.format
import com.gestion.despacho.utils.OperationResult
import com.gestion.despacho.utils.PermissionsAwareActivity.getString
import com.gestion.despacho.utils.SessionManager
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MaterialPickingViewModel : ViewModel() {

    private var _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    private var _loader: MutableLiveData<Boolean> = MutableLiveData()
    val loader: LiveData<Boolean> = _loader

    private var _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private var _pdf: MutableLiveData<Boolean> = MutableLiveData()
    val pdf: LiveData<Boolean> = _pdf

    private var _response: MutableLiveData<Boolean> = MutableLiveData()
    val response: LiveData<Boolean> = _response

    private var _observe: MutableLiveData<Boolean> = MutableLiveData()
    val observe: LiveData<Boolean> = _observe

    private var _getObserve: MutableLiveData<Boolean> = MutableLiveData()
    val getObserve: LiveData<Boolean> = _getObserve

    private var _success: MutableLiveData<Picking> = MutableLiveData()
    val success: LiveData<Picking> = _success

    private var _picking: MutableLiveData<ClsPickingDetail> = MutableLiveData()
    val picking: LiveData<ClsPickingDetail> = _picking

    private var _nbrPicking: MutableLiveData<String> = MutableLiveData()

    val pickingDet = PickingRepositoryImp.Picking.getPickingDB

    private var pickingRepository: PickingRepository = PickingRepositoryImp()

    fun addStevedor(nombre: String, dni: String, entity: ClsPickingDetail) {
        viewModelScope.launch {
            try {
                _loader.value = true
                val objStevedor = ClsStevedores(
                    0,
                    entity.IdPicking,
                    entity.cod_sap_material,
                    entity.type_load,
                    entity.material,
                    nombre,
                    dni
                )
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.addStevedor(objStevedor)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        if (response.data == 200) {
                            when (val responseDb = withContext(Dispatchers.IO) {
                                pickingRepository.saveStevedor(objStevedor)
                            }) {
                                is OperationResult.Complete -> _message.value = responseDb.data
                                is OperationResult.Failure -> _error.value = responseDb.toString()
                            }
                        }
                    }

                    is OperationResult.Failure -> _error.value =
                        response.exception?.message.toString()
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loader.value = false
            }
        }
    }

    fun startLoad(entity: ClsPickingDetail, edtNbrLot: String) {
        viewModelScope.launch {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.startLoadApi(entity, edtNbrLot)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        if (response.data == 200) {
                            when (val responseDb = withContext(Dispatchers.IO) {
                                pickingRepository.startLoad(entity, edtNbrLot)
                            }) {
                                is OperationResult.Complete -> _message.value = responseDb.data
                                is OperationResult.Failure -> _error.value =
                                    responseDb.exception?.message.toString()
                            }
                        }
                    }

                    is OperationResult.Failure -> _error.value =
                        response.exception?.message.toString()
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loader.value = false
            }
        }
    }

    fun endLoad(entity: ClsPickingDetail) {
        viewModelScope.launch {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.endLoadApi(entity)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        if (response.data == 200) {
                            when (val responseDb = withContext(Dispatchers.IO) {
                                pickingRepository.endLoad(entity)
                            }) {
                                is OperationResult.Complete -> _message.value = responseDb.data
                                is OperationResult.Failure -> _error.value =
                                    responseDb.exception?.message.toString()
                            }
                        }
                    }

                    is OperationResult.Failure -> _error.value =
                        response.exception?.message.toString()
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loader.value = false
            }
        }
    }

    fun checkStevedores(entity: ClsPickingDetail) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.checkStevedores(entity)
                }

                when (response) {
                    is OperationResult.Complete -> _picking.value = response.data
                    is OperationResult.Failure -> _error.value =
                        response.exception?.message.toString()
                }

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun observePicking(id: String, observation: String) {
        viewModelScope.launch {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.observePickingApi(id, observation)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        if (response.data == 200) {
                            when (val responseDb = withContext(Dispatchers.IO) {
                                pickingRepository.observePicking(id, observation)
                            }) {
                                is OperationResult.Complete -> {
                                    _message.value = responseDb.data
                                    _getObserve.value = true
                                }

                                is OperationResult.Failure -> _error.value =
                                    responseDb.exception?.message.toString()
                            }
                        }
                    }

                    is OperationResult.Failure -> _error.value =
                        response.exception?.message.toString()
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loader.value = false
            }
        }
    }


    fun checkPicking(id: String, action: String) {
        viewModelScope.launch {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.checkPicking(id)
                }
                handlePickingResponse(response, id, action)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loader.value = false
            }
        }
    }

    private suspend fun handlePickingResponse(
        response: OperationResult<Int>, id: String, action: String
    ) {
        when (response) {
            is OperationResult.Complete -> {
                if (response.data == 1) {
                    val responseVerified = withContext(Dispatchers.IO) {
                        pickingRepository.verifiedPicking(id)
                    }
                    handleVerifiedResponse(responseVerified, id, action)
                }
            }

            is OperationResult.Failure -> _error.value = response.exception?.message.toString()
        }
    }

    private fun handleVerifiedResponse(
        responseVerified: OperationResult<Int>, id: String, action: String
    ) {
        when (responseVerified) {
            is OperationResult.Complete -> {
                if (responseVerified.data == 200) {
                    handleAction(action)
                    _nbrPicking.value = id
                    saveVerificationData()
                }
            }

            is OperationResult.Failure -> _error.value =
                responseVerified.exception?.message.toString()
        }
    }

    private fun handleAction(action: String) {
        when (action) {
            Constants.VALIDATE -> _response.value = true
            Constants.OBSERVE -> _observe.value = true
        }
    }

    private fun saveVerificationData() {
        SessionManager().saveDateVerify(getDate())
        SessionManager().saveHourVerify(getHour())
    }

    fun sendMail(id: String, context: Context) {
        viewModelScope.launch {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.getPicking(id)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        var resp = true
                        response.data?.let {
                            withContext(Dispatchers.IO) {
                                val respuesta = MailRepository().getMails()
                                if (respuesta == 1) {
                                    MailRepository().execute(it, context).onErrorComplete {
                                            resp = false
                                            Log.e("ASCT", it.message ?: it.toString())
                                            true
                                        }.subscribeOn(Schedulers.io()).subscribe()
                                } else {
                                    resp = false
                                }
                            }
                            if (resp) _success.value = response.data.Data
                            else _message.value = context.getString(R.string.error_recibir_correos)
                        }
                    }

                    is OperationResult.Failure -> {
                        _error.value = response.exception?.message.toString()
                        _response.value = false
                    }
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loader.value = false
            }
        }
    }

    fun observeMaterial(material: ClsPickingDetail?, motivo: String) {
        viewModelScope.launch {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.observeMaterialApi(material, motivo)
                }
                handleObserveMaterialApiResponse(response, material, motivo)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loader.value = false
            }
        }
    }

    private suspend fun handleObserveMaterialApiResponse(
        response: OperationResult<Int>, material: ClsPickingDetail?, motivo: String
    ) {
        when (response) {
            is OperationResult.Complete -> {
                if (response.data == 200) {
                    handleDatabaseResponse(material, motivo)
                }
            }

            is OperationResult.Failure -> _error.value = response.toString()
        }
    }

    private suspend fun handleDatabaseResponse(material: ClsPickingDetail?, motivo: String) {
        when (val responseDb = withContext(Dispatchers.IO) {
            pickingRepository.observeMaterial(material, motivo)
        }) {
            is OperationResult.Complete -> {
                if (responseDb.data == 1) {
                    _message.value = getString(R.string.material_observado_correctamente)
                }
            }

            is OperationResult.Failure -> _error.value = responseDb.exception?.message.toString()
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

    fun loadData(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                _loader.value = true

                val response = withContext(Dispatchers.IO) {
                    pickingRepository.getPicking(id)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        if (response.data?.Code == 200) {
                            _message.value = "SUCCESS"
                        } else {
                            _error.value = response.data?.Description
                        }
                    }

                    is OperationResult.Failure -> {
                        _error.value = response.exception?.message.toString()
                    }
                }

            } catch (e: Exception) {
                _error.value = e.message.toString()
            } finally {
                _loader.value = false
            }

        }
    }
}