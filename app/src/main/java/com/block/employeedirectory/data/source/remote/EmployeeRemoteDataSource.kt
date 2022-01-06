package com.block.employeedirectory.data.source.remote

import com.block.employeedirectory.data.api.EmployeeService
import com.block.employeedirectory.data.model.EmployeeDTO
import com.block.employeedirectory.data.model.Response
import java.io.IOException
import javax.inject.Inject

class EmployeeRemoteDataSource @Inject constructor(private val service: EmployeeService) {

    suspend fun <T : Any>loadEmployees() : Response<T> {
        return try {
            retrieveEmployeeData() as Response<T>
        } catch (e: Exception) {
            Response.Error(IOException("Temp api error message", e))
        }
    }

    suspend fun <T : Any>loadUnstableEmployees() : Response<T> {
        return try {
            retrieveRandomizedEmployeeData() as Response<T>
        } catch (e: Exception) {
            Response.Error(IOException("Temp api error message", e))
        }
    }

    private suspend fun retrieveEmployeeData(): Response<EmployeeDTO> {
        val response = service.getEmployees()

        if(response.isSuccessful) {
            val employeeData = response.body()
            if(employeeData != null) {
                return Response.Success(employeeData)
            }
        }

        return Response.Error(IOException("Temp api error response message" +
                "${response.code()} ${response.message()}"))
    }

    //randomly picks the endpoint to make network call to - in order to view various view states
    private suspend fun retrieveRandomizedEmployeeData(): Response<EmployeeDTO> {
        val response = randomUnstableEmployeeData()

        if(response.isSuccessful) {
            val employeeData = response.body()
            if(employeeData != null) {
                return Response.Success(employeeData)
            }
        }

        return Response.Error(IOException("Temp api error response message" +
                "${response.code()} ${response.message()}"))
    }

    //randomly picks the endpoint to make network call to - in order to view various view states
    private suspend fun randomUnstableEmployeeData(): retrofit2.Response<EmployeeDTO> {
        return when (UnstableEmployeeDataType.values().random()) {
            UnstableEmployeeDataType.STABLE -> service.getEmployees()
            UnstableEmployeeDataType.EMPTY -> service.getEmployeesEmpty()
            UnstableEmployeeDataType.MALFORMED -> service.getEmployeesMalformed()
        }
    }
}

//TODO consider moving out of this class to data class
enum class UnstableEmployeeDataType {
    STABLE, EMPTY, MALFORMED
}