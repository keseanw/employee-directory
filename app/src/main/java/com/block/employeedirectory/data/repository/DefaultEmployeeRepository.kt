package com.block.employeedirectory.data.repository

import androidx.annotation.VisibleForTesting
import com.block.employeedirectory.data.CoroutinesDispatcherProvider
import com.block.employeedirectory.data.model.EmployeeDTO
import com.block.employeedirectory.data.model.Response
import com.block.employeedirectory.data.source.remote.EmployeeRemoteDataSource
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface EmployeeRepository {
    suspend fun getEmployees() : Response<EmployeeDTO>
    suspend fun getEmployeesUnstable() : Response<EmployeeDTO>
}


class DefaultEmployeeRepository @Inject constructor
    (@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
     private val employeeRemoteDataSource: EmployeeRemoteDataSource,
     private val dispatcherProvider: CoroutinesDispatcherProvider): EmployeeRepository{

    override suspend fun getEmployees() : Response<EmployeeDTO> {
        return withContext(dispatcherProvider.io) {
            return@withContext employeeRemoteDataSource.loadEmployees()
        }
    }

    override suspend fun getEmployeesUnstable() : Response<EmployeeDTO> {
        return withContext(dispatcherProvider.io) {
            return@withContext employeeRemoteDataSource.loadUnstableEmployees()
        }
    }
}