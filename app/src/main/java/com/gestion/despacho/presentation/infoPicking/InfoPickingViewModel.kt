package com.gestion.despacho.presentation.infoPicking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestion.despacho.model.ClsPicking
import com.gestion.despacho.repository.picking.PickingRepository
import com.gestion.despacho.repository.picking.PickingRepositoryImp
import com.gestion.despacho.utils.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoPickingViewModel : ViewModel() {

    private var _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    private var _loader: MutableLiveData<Boolean> = MutableLiveData()
    val loader: LiveData<Boolean> = _loader

    private var _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private var _picking: MutableLiveData<ClsPicking> = MutableLiveData()
    val picking: LiveData<ClsPicking> = _picking

    private var pickingRepository: PickingRepository = PickingRepositoryImp()
    val infoPicking = PickingRepositoryImp.Picking.getInfoPickingDB
    fun getInfoPicking(id: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    pickingRepository.getInfoPicking(id = id)
                }

                when (response) {
                    is OperationResult.Complete -> {
                        _picking.value = response.data
                    }
                    is OperationResult.Failure -> {
                        _error.value = response.exception?.message.toString()
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
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