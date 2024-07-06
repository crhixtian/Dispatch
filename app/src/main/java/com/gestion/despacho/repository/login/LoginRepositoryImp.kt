package com.gestion.despacho.repository.login

import com.gestion.despacho.model.LoginDto
import com.gestion.despacho.model.LoginRequest
import com.gestion.despacho.data.remote.Api
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.OperationResult

class LoginRepositoryImp: LoginRepository {
    override suspend fun authenticate(user: String, pass: String): OperationResult<LoginDto> {

        return try{

            val request = LoginRequest(
                User = user,
                Password = pass,
                CodigoModulo = Constants.MODULE_CODE)

            val response = Api.build().authenticateCredentials(request)

            if(response.isSuccessful){
                OperationResult.Complete(response.body())
            }else{
                OperationResult.Failure(java.lang.Exception(response.body()?.Description))
            }

        }catch (e: Exception){
            OperationResult.Failure(e)
        }
    }
}