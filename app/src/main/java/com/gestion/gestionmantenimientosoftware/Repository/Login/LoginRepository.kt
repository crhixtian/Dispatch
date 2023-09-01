package com.gestion.gestionmantenimientosoftware.Repository.Login

import com.gestion.gestionmantenimientosoftware.Model.LoginDto
import com.gestion.gestionmantenimientosoftware.Utils.OperationResult

interface LoginRepository {

    suspend fun authenticate(user: String, pass: String) : OperationResult<LoginDto>
}