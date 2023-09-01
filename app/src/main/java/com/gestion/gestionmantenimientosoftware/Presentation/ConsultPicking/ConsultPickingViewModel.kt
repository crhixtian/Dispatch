package com.gestion.gestionmantenimientosoftware.Presentation.ConsultPicking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestion.gestionmantenimientosoftware.Repository.Picking.PickingRepository
import com.gestion.gestionmantenimientosoftware.Repository.Picking.PickingRepositoryImp
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.OperationResult
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

    var pickingRepository: PickingRepository = PickingRepositoryImp()

    fun getPicking(idPicking: String){
        viewModelScope.launch(Dispatchers.Main) {
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
    /*private fun cleanDB(picking: Picking) {
        viewModelScope.launch(Dispatchers.Main) {
            try{
                _loader.value = true
                val response = withContext(Dispatchers.IO){
                    pickingRepository.cleanDB()
                }

                /*when(response){
                    is OperationResult.Complete ->{
                        if(response.data == Constants.RESPONSE_SUCCESSFULLY){
                         savePicking(picking)
                        }
                    }
                    is OperationResult.Failure -> {
                        _error.value = response.exception?.message.toString()
                    }
                }*/
            }catch (e: Exception){
                _error.value = e.message
            }finally {
                _loader.value = false
            }
        }
    }*/
    /*private fun savePicking(picking: Picking) {

        viewModelScope.launch(Dispatchers.Main){
            try{
                _loader.value = true
                val response = withContext(Dispatchers.IO){
                    pickingRepository.savePicking(picking)
                }

                /*when(response){
                    is OperationResult.Complete ->{
                        //_message.value = response.data
                        savePickingDetails(picking.pickingDet)
                    }
                    is OperationResult.Failure -> {
                        _error.value = response.exception?.message.toString()
                    }
                }*/

            }catch (e: Exception){
                _error.value = e.message
            }finally {
                _loader.value = false
            }
        }
    }*/

    /*private fun savePickingDetails(picking: List<PickingDetail>) {

        viewModelScope.launch(Dispatchers.Main){
            try{
                _loader.value = true
                val response = withContext(Dispatchers.IO){
                    pickingRepository.savePickingDetail(picking)
                }

                /*when(response){

                    is OperationResult.Complete ->{
                        if(response.data == Constants.RESPONSE_SUCCESSFULLY){
                            _message.value = Constants.RESPONSE_SUCCESSFULLY_DETAILS
                            _pass.value = true
                        }
                    }
                    is OperationResult.Failure -> {
                        _error.value = response.exception?.message.toString()
                        _pass.value = false
                    }
                }*/

            }catch (e: Exception){
                _error.value = e.message
                _pass.value = false
            }finally {
                _loader.value = false
            }
        }
    }*/

}