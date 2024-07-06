package com.gestion.despacho.repository.login

import com.gestion.despacho.model.LoginDto
import com.gestion.despacho.utils.OperationResult

interface LoginRepository {

    suspend fun authenticate(user: String, pass: String) : OperationResult<LoginDto>
}