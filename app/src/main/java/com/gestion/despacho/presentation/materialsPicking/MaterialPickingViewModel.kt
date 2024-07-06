package com.gestion.gestionmantenimientosoftware.Presentation.MaterialsPicking

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestion.gestionmantenimientosoftware.Model.ClsPickingDetail
import com.gestion.gestionmantenimientosoftware.Model.ClsStevedores
import com.gestion.gestionmantenimientosoftware.Model.Picking
import com.gestion.gestionmantenimientosoftware.Repository.Mail.MailRepository
import com.gestion.gestionmantenimientosoftware.Repository.Pdf.PdfRepository
import com.gestion.gestionmantenimientosoftware.Repository.Pdf.PdfRepositoryImp
import com.gestion.gestionmantenimientosoftware.Repository.Picking.PickingRepository
import com.gestion.gestionmantenimientosoftware.Repository.Picking.PickingRepositoryImp
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.Format.format
import com.gestion.gestionmantenimientosoftware.Utils.OperationResult
import com.gestion.gestionmantenimientosoftware.Utils.SessionManager
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

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
    val nbrPicking: LiveData<String> = _nbrPicking

    val pickingDet = PickingRepositoryImp.Picking.getPickingDB

    var pickingRepository: PickingRepository = PickingRepositoryImp()
    var pdfRepository: PdfRepository = PdfRepositoryImp()
    //var mailRepository: MailRepository = MailRepositoryImp()

    fun addStevedor(nombre: String, dni: String, entity: ClsPickingDetail) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                _loader.value = true
                val objStevedor = ClsStevedores(
                    0, entity.IdPicking,
                    entity.cod_sap_material, entity.type_load, entity.material,
                    nombre, dni
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
        viewModelScope.launch(Dispatchers.Main) {
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
        viewModelScope.launch(Dispatchers.Main) {
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
        viewModelScope.launch(Dispatchers.Main) {
            try {
                //_loader.value = true
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
            } //finally {
            //_loader.value = false
            //}
        }
    }

    fun observePicking(id: String, observation: String) {
        viewModelScope.launch(Dispatchers.Main) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPicking(id: String, action: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.checkPicking(id)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        if (response.data == 1) {
                            val responseVerified = withContext(Dispatchers.IO) {
                                pickingRepository.verifiedPicking(id)
                            }
                            when (responseVerified) {
                                is OperationResult.Complete ->
                                    if (responseVerified.data == 200) {
                                        when (action) {
                                            Constants.VALIDATE -> _response.value = true
                                            Constants.OBSERVE -> _observe.value = true
                                        }
                                        _nbrPicking.value = id
                                        SessionManager().saveDateVerify(getDate())
                                        SessionManager().saveHourVerify(getHour())
                                    }
                                is OperationResult.Failure -> _error.value =
                                    responseVerified.exception?.message.toString()
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

    fun sendMail(id: String, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
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
                                    MailRepository().execute(it, context)
                                        .onErrorComplete {
                                            resp = false
                                            Log.e("ASCT", it.message ?: it.toString())
                                            true
                                        }.subscribeOn(Schedulers.io())
                                        .subscribe()
                                } else {
                                    resp = false
                                }
                            }
                            if (resp) _success.value = response.data.Data
                            else _message.value = "Error al recibir correos"
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
        viewModelScope.launch(Dispatchers.Main) {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.observeMaterialApi(material, motivo)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        if (response.data == 200) {
                            when (val responseDb = withContext(Dispatchers.IO) {
                                pickingRepository.observeMaterial(material, motivo)
                            }) {
                                is OperationResult.Complete -> {
                                    if (responseDb.data == 1){
                                        _message.value = "Material observado correctamente"
                                    }
                                }
                                is OperationResult.Failure -> _error.value =
                                    responseDb.exception?.message.toString()
                            }
                        }
                    }
                    is OperationResult.Failure -> _error.value = response.toString()
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loader.value = false
            }
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