package com.android.dispatch.presentation.validatePicking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dispatch.model.Picking
import com.android.dispatch.repository.picking.PickingRepository
import com.android.dispatch.repository.picking.PickingRepositoryImp
import com.android.dispatch.utils.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PickingSuccesViewModel: ViewModel() {

    private var _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    private var _loader: MutableLiveData<Boolean> = MutableLiveData()
    val loader: LiveData<Boolean> = _loader

    private var _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private var _picking: MutableLiveData<Picking> = MutableLiveData()
    val picking: LiveData<Picking> = _picking

    var pickingRepository: PickingRepository = PickingRepositoryImp()

    fun getInfoPicking(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try{
                _loader.value = true
                val response = withContext(Dispatchers.IO){
                    pickingRepository.getPicking(id)
                }

                when(response){
                    is OperationResult.Complete -> _picking.value = response.data?.Data
                    is OperationResult.Failure -> _message.value = response.exception?.message.toString()
                }
            }catch (e: Exception){
                _error.value = e.message.toString()
            }finally {
                _loader.value = false
            }
        }
    }
}