package com.android.dispatch.repository.login

import com.android.dispatch.data.remote.Api
import com.android.dispatch.model.LoginDto
import com.android.dispatch.model.LoginRequest
import com.android.dispatch.utils.Constants
import com.android.dispatch.utils.OperationResult


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