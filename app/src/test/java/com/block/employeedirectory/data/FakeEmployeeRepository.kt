package com.block.employeedirectory.data

import com.block.employeedirectory.data.model.EmployeeDTO
import com.block.employeedirectory.data.model.Response
import com.block.employeedirectory.data.repository.EmployeeRepository
import org.mockito.Mockito

class FakeEmployeeRepository: EmployeeRepository {

    var shouldReturnError = false

    override suspend fun getEmployees(): Response<EmployeeDTO> {
        if (shouldReturnError) {
            return Response.Error(Exception("getEmployees() failed"))
        }
        return Response.Success(Mockito.mock(EmployeeDTO::class.java))

    }

    override suspend fun getEmployeesUnstable(): Response<EmployeeDTO> {
        if (shouldReturnError) {
            return Response.Error(Exception("getEmployeesUnstable() failed"))
        }
        return Response.Success(Mockito.mock(EmployeeDTO::class.java))
    }
}