package com.block.employeedirectory.ui.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.block.employeedirectory.data.model.Employee
import com.block.employeedirectory.data.model.Response
import com.block.employeedirectory.data.repository.EmployeeRepository
import com.block.employeedirectory.ui.model.EmployeeUiState
import com.block.employeedirectory.ui.model.ScreenState
import com.block.employeedirectory.ui.model.toEmployeeItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor (private val repository: EmployeeRepository): ViewModel() {

    var employeeUiState by mutableStateOf(EmployeeUiState())
        private set

    var employeeUiScreenState by mutableStateOf(ScreenState.EMPTY)

    private var fetchJob: Job? = null

    fun getEmployees() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val employeeDto = repository.getEmployees()
                employeeUiScreenState = ScreenState.LOADING
                employeeUiState = EmployeeUiState(employeeUiScreenState, listOf())

                //extract this into function
                when (employeeDto) {
                    is Response.Success -> {
                        employeeUiScreenState = if(employeeDto.data.employees.isNullOrEmpty()) ScreenState.EMPTY
                        else ScreenState.DATA

                        //consider adding to a thread todo
                        val employees = removeEmployeesThatMatchFilters(employeeDto.data.employees).also { getDistinctEmployeeUuid(it) }.also { getDistinctEmployeeEmail(it) }
                        val employeeUiItems = employees.map { it.toEmployeeItemUiState() }
                        employeeUiState = EmployeeUiState(employeeUiScreenState, employeeItems = employeeUiItems)
                    }
                    is Response.Error -> {
                        employeeUiScreenState = ScreenState.ERROR
                        employeeUiState = EmployeeUiState(employeeUiScreenState, listOf(), listOf(employeeDto.exception.message ?: "Error"))
                    }
                }

            } catch (ioe: IOException) {
                print("${ioe.message}")
                employeeUiScreenState = ScreenState.ERROR
                employeeUiState = EmployeeUiState(employeeUiScreenState, listOf(), listOf(ioe.message ?: "Error"))

            }
        }
    }

    @VisibleForTesting
    fun removeEmployeesThatMatchFilters(list: List<Employee>): List<Employee> {

        val conditions = ArrayList<(Employee) -> Boolean>()

        conditions.add{ employee ->  employee.uuid.isNullOrBlank() }
        conditions.add{ employee ->  employee.name.isNullOrBlank() }
        conditions.add{ employee ->  employee.email.isNullOrBlank() }
        conditions.add{ employee ->  employee.team.isNullOrBlank() }
        conditions.add{ employee ->  employee.employeeType == null }

        return list.filterNot { candidate -> conditions.any { it(candidate) } }
    }

    @VisibleForTesting
    fun getDistinctEmployeeUuid(employeeList: List<Employee>) = employeeList.distinctBy { it.uuid }

    @VisibleForTesting
    fun getDistinctEmployeeEmail(employeeList: List<Employee>) = employeeList.distinctBy { it.email }

}