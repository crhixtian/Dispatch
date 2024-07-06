package com.gestion.despacho.presentation.stevedores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestion.despacho.model.ClsStevedores
import com.gestion.despacho.repository.picking.PickingRepository
import com.gestion.despacho.repository.picking.PickingRepositoryImp
import com.gestion.despacho.utils.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StevedoresViewModel : ViewModel() {

    private var _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    private var _loader: MutableLiveData<Boolean> = MutableLiveData()
    val loader: LiveData<Boolean> = _loader

    private var _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private var pickingRepository: PickingRepository = PickingRepositoryImp()
    val stevedores = PickingRepositoryImp.Picking.getStevedores

    fun edit(stevedor: ClsStevedores) {
        viewModelScope.launch {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.updateStevedoresApi(stevedor)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        if (response.data == 200) {
                            when (val responseDb = withContext(Dispatchers.IO) {
                                pickingRepository.updateStevedores(stevedor)
                            }) {
                                is OperationResult.Complete -> {
                                    _message.value = responseDb.data
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

    fun delete(stevedor: ClsStevedores) {
        viewModelScope.launch {
            try {
                _loader.value = true
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.deleteStevedorApi(stevedor)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        if (response.data == 200) {
                            when (val responseDb = withContext(Dispatchers.IO) {
                                pickingRepository.deleteStevedor(stevedor)
                            }) {
                                is OperationResult.Complete -> {
                                    _message.value = responseDb.data
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

    fun loadData(id: String) {
        viewModelScope.launch {
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