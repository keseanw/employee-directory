package com.block.employeedirectory.data.api

import com.block.employeedirectory.data.model.EmployeeDTO
import retrofit2.Response
import retrofit2.http.GET

interface EmployeeService {

    @GET("employees.json")
    suspend fun getEmployees(): Response<EmployeeDTO>

    @GET("employees_malformed.json")
    suspend fun getEmployeesMalformed(): Response<EmployeeDTO>

    @GET("employees_empty.json")
    suspend fun getEmployeesEmpty(): Response<EmployeeDTO>
}