package com.android.dispatch.repository.login

import com.android.dispatch.model.LoginDto
import com.android.dispatch.utils.OperationResult

interface LoginRepository {

    suspend fun authenticate(user: String, pass: String) : OperationResult<LoginDto>
}