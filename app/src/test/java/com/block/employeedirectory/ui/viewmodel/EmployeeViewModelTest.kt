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
 * Tests only
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

    @Before
    fun setup() {
        employeeRepository = FakeEmployeeRepository()
        employeeViewModel = EmployeeViewModel(employeeRepository)

        employeeList = listOf(Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Camille Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME), Employee(uuid = "a98f8a2e-c975-4ba3-8b35-01f719e7de2d",
            name = "Tom Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME))

    }


    @Test
    fun `when given a list of employees with 1 missing uuid, filter the employee out of the list`() {

        val malformedEmployeeUuidList = listOf(Employee(uuid = null,
            name = "Camille Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME), Employee(uuid = "a98f8a2e-c975-4ba3-8b35-01f719e7de2d",
            name = "Tom Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME))

        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeUuidList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with 1 missing name, filter the employee out of the list`() {

        val malformedEmployeeNameList = listOf(Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = null, phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME), Employee(uuid = "a98f8a2e-c975-4ba3-8b35-01f719e7de2d",
            name = "Tom Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME))

        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeNameList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with 1 missing email, filter the employee out of the list`() {

        val malformedEmployeeEmailList = listOf(Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Camille Rogers", phoneNumber = "5558531970", email = null,
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME), Employee(uuid = "a98f8a2e-c975-4ba3-8b35-01f719e7de2d",
            name = "Tom Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME))

        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeEmailList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with 1 missing team, filter the employee out of the list`() {

        val malformedEmployeeTeamList = listOf(Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Camille Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "", employeeType = EmployeeType.PART_TIME), Employee(uuid = "a98f8a2e-c975-4ba3-8b35-01f719e7de2d",
            name = "Tom Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME))

        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeTeamList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with 1 missing employeeType, filter the employee out of the list`() {

        val malformedEmployeeTypeList = listOf(Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Camille Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Designer on the web marketing team.", employeeType = null), Employee(uuid = "a98f8a2e-c975-4ba3-8b35-01f719e7de2d",
            name = "Tom Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME))

        val removedMalformedEmployeeData = employeeViewModel.removeEmployeesThatMatchFilters(malformedEmployeeTypeList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Tom Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with matching uuids, filter the employee out of the list`() {

        val malformedEmployeeTypeList = listOf(Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Camille Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Designer on the web marketing team.", employeeType = null), Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Tom Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME))

        val removedMalformedEmployeeData = employeeViewModel.getDistinctEmployeeUuid(malformedEmployeeTypeList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Camille Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with matching emails, filter the employee out of the list`() {

        val malformedEmployeeTypeList = listOf(Employee(uuid = "0d8fcc13-4d0c-425c-8355-390b312b909c",
            name = "Camille Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Designer on the web marketing team.", employeeType = null), Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Tom Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME))

        val removedMalformedEmployeeData = employeeViewModel.getDistinctEmployeeEmail(malformedEmployeeTypeList)

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Camille Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }

    @Test
    fun `when given a list of employees with both matching emails & uuids, filter the employees out of the list`() {

        val malformedEmployeeTypeList = listOf(Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Camille Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Designer on the web marketing team.", employeeType = null), Employee(uuid = "0d8fcc12-4d0c-425c-8355-390b312b909c",
            name = "Tom Rogers", phoneNumber = "5558531970", email = "crogers.demo@squareup.com",
            bio = "Designer on the web marketing team.", photoSmall = "https://s3.amazonaws.com/sq-mobile-interview/photos/",
            photoLarge = "https://s3.amazonaws.com/sq-mobile-interview/photos/", team = "Public Web & Marketing", employeeType = EmployeeType.PART_TIME))

        val removedMalformedEmployeeData = employeeViewModel.getDistinctEmployeeEmail(malformedEmployeeTypeList).also { employeeViewModel.getDistinctEmployeeUuid(it) }

        Truth.assertThat(removedMalformedEmployeeData[0].name).isEqualTo("Camille Rogers")
        Truth.assertThat(removedMalformedEmployeeData.size).isEqualTo(1)
    }
}