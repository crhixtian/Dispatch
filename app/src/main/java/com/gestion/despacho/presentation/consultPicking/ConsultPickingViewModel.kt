package com.gestion.despacho.presentation.consultPicking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestion.despacho.repository.picking.PickingRepository
import com.gestion.despacho.repository.picking.PickingRepositoryImp
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConsultPickingViewModel: ViewModel() {

    private var _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    private var _loader: MutableLiveData<Boolean> = MutableLiveData()
    val loader: LiveData<Boolean> = _loader

    private var _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private var _pass: MutableLiveData<Boolean> = MutableLiveData()
    val pass: LiveData<Boolean> = _pass

    private var _canceled: MutableLiveData<Boolean> = MutableLiveData()
    val canceled: LiveData<Boolean> = _canceled

    private var _nbrPicking: MutableLiveData<String> = MutableLiveData()
    val nbrPicking: LiveData<String> = _nbrPicking

    private var pickingRepository: PickingRepository = PickingRepositoryImp()

    fun getPicking(idPicking: String){
        viewModelScope.launch {
            try {
                _loader.value = true

                val response = withContext(Dispatchers.IO){
                    pickingRepository.getPicking(idPicking)
                }

                when(response){
                    is OperationResult.Complete ->{
                        if(response.data?.Code == 200){
                            _nbrPicking.value = response.data.Data.nbrpicking
                            if(response.data.Data.pickingDet.isEmpty()){
                                _canceled.value = true
                            }else{
                                _message.value = Constants.RESPONSE_SUCCESSFULLY_DETAILS
                                _pass.value = true
                            }
                        }else{
                            _error.value = response.data?.Description
                            _pass.value = false
                        }
                    }
                    is OperationResult.Failure -> {
                        _error.value = response.exception?.message.toString()
                    }
                }

            }catch (e: Exception){
                _error.value = e.message.toString()
            }finally {
                _loader.value = false
            }

        }
    }
}
