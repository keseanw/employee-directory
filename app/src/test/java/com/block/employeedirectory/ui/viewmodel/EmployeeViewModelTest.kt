package com.block.employeedirectory.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.block.employeedirectory.data.FakeEmployeeRepository
import com.block.employeedirectory.data.model.Employee
import com.block.employeedirectory.data.model.EmployeeType
import com.block.employeedirectory.ui.model.ScreenState
import com.block.employeedirectory.ui.viewmodel.coroutines.MainCoroutineRule
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
* Employee ViewModel Test
* */
@ExperimentalCoroutinesApi
class EmployeeViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var employeeViewModel: EmployeeViewModel

    // Use a fake repository to be injected into the viewModel
    private lateinit var employeeRepository: FakeEmployeeRepository
    private var employeeList: List<Employee> = listOf()
    private lateinit var perfectEmployee: Employee

    @Before
    fun setup() {
        employeeRepository = FakeEmployeeRepository()
        employeeViewModel = EmployeeViewModel(employeeRepository)

        perfectEmployee = Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Camille Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME)


        employeeList = listOf(perfectEmployee.copy(),
            perfectEmployee.copy(uuid = "a98f8a2e-c975-4ba3-8b35-01f719e7de2d",
                name = "Tom Rogers", email = "crogers4.demo@squareup.com"))

    }


    @Test
    fun `when given a list of employees with 1 missing uuid, filter the employee out of the list`() {

        val malformedEmployee = perfectEmployee.copy(uuid = null)
        val validEmployee = perfectEmployee.copy(name = "Tom Rogers")
        val malformedEmployeeUuidList = listOf(malformedEmployee, validEmployee)

        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeUuidList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with 1 missing name, filter the employee out of the list`() {

        val malformedEmployee = perfectEmployee.copy(name = null)
        val validEmployee = perfectEmployee.copy(name = "Tom Rogers")
        val malformedEmployeeNameList = listOf(malformedEmployee, validEmployee)
        
        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeNameList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with 1 missing email, filter the employee out of the list`() {

        val malformedEmployee = perfectEmployee.copy(email = null)
        val validEmployee = perfectEmployee.copy(name = "Tom Rogers")
        val malformedEmployeeEmailList = listOf(malformedEmployee, validEmployee)

        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeEmailList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with 1 missing team, filter the employee out of the list`() {

        val malformedEmployee = perfectEmployee.copy(team = null)
        val validEmployee = perfectEmployee.copy(name = "Tom Rogers")
        val malformedEmployeeTeamList = listOf(malformedEmployee, validEmployee)

        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeTeamList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with 1 missing employeeType, filter the employee out of the list`() {

        val malformedEmployee = perfectEmployee.copy(employeeType = null)
        val validEmployee = perfectEmployee.copy(name = "Tom Rogers")
        val malformedEmployeeTypeList = listOf(malformedEmployee, validEmployee)

        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeTypeList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with matching uuids, filter the employee out of the list`() {

        val malformedEmployee = perfectEmployee.copy(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c")
        val malformedEmployee2 = perfectEmployee.copy(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Tom Rogers")
        val malformedEmployeeTypeList = listOf(malformedEmployee, malformedEmployee2)

        val removedMalformedEmployeeData = employeeViewModel.getDistinctEmployeeUuid(malformedEmployeeTypeList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Camille Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with matching emails, filter the employee out of the list`() {

        val malformedEmployee = perfectEmployee.copy(email = "crogers.demo@squareup.com")
        val malformedEmployee2 = perfectEmployee.copy(email = "crogers.demo@squareup.com",
            name = "Tom Rogers")
        val malformedEmployeeTypeList = listOf(malformedEmployee, malformedEmployee2)

        val removedMalformedEmployeeData = employeeViewModel.getDistinctEmployeeEmail(malformedEmployeeTypeList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Camille Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with both matching emails & uuids, filter the employees out of the list`() {

        val malformedEmployee = perfectEmployee.copy(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            email = "crogers.demo@squareup.com")
        val malformedEmployee2 = perfectEmployee.copy( "0d8fcc12-4d0c-425c-8355-390b312b909c",
            email = "crogers.demo@squareup.com",
            name = "Tom Rogers")
        val malformedEmployeeTypeList = listOf(malformedEmployee, malformedEmployee2)

        val removedMalformedEmployeeData = employeeViewModel.getDistinctEmployeeEmail(malformedEmployeeTypeList).also { employeeViewModel.getDistinctEmployeeUuid(it) }

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Camille Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }
}