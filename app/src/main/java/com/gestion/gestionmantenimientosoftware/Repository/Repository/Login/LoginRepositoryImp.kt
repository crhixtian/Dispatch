package com.gestion.gestionmantenimientosoftware.Repository.Login

import com.gestion.gestionmantenimientosoftware.Data.Remote.Api
import com.gestion.gestionmantenimientosoftware.Model.LoginDto
import com.gestion.gestionmantenimientosoftware.Model.LoginRequest
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.OperationResult

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