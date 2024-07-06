package com.gestion.despacho.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestion.despacho.model.User
import com.gestion.despacho.repository.login.LoginRepository
import com.gestion.despacho.repository.login.LoginRepositoryImp
import com.gestion.despacho.utils.OperationResult
import com.gestion.despacho.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel: ViewModel() {

    private var _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    private var _loader: MutableLiveData<Boolean> = MutableLiveData()
    val loader: LiveData<Boolean> = _loader

    private var _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user

    private var loginRepository: LoginRepository = LoginRepositoryImp()

    fun authenticate(user: String, pass: String){
        viewModelScope.launch {
            try{
                _loader.value = true
                val response = withContext(Dispatchers.IO){
                    loginRepository.authenticate(user, pass)
                }

                when(response){
                    is OperationResult.Complete -> {
                        if(response.data?.Code == 200){
                            _user.value = response.data.Data
                            SessionManager().saveStatuSession(true)
                            SessionManager().saveUser(response.data.Data.FullName)
                            SessionManager().saveRolId(response.data.Data.RolId)
                        }else{
                            _error.value = response.data?.Description
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