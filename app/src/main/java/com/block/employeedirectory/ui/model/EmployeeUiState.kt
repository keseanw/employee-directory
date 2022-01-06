package com.block.employeedirectory.ui.model

import com.block.employeedirectory.data.model.Employee
import com.block.employeedirectory.data.model.EmployeeType

data class EmployeeUiState(
    val screenState: ScreenState = ScreenState.EMPTY,
    val employeeItems: List<EmployeeItemUiState> = listOf(),
    val errorMessages: List<String> = listOf()
)

data class EmployeeItemUiState(
    val uuid: String?,
    val name: String?,
    val phoneNumber: String?,
    val email: String?,
    val bio: String?,
    val photoSmall: String?,
    val photoLarge: String?,
    val team: String?,
    val employeeType: EmployeeType?,
)

fun Employee.toEmployeeItemUiState() = EmployeeItemUiState (
    uuid = uuid,
    name = name,
    phoneNumber = phoneNumber,
    email = email,
    bio = bio,
    photoSmall = photoSmall,
    photoLarge = photoLarge,
    team = team,
    employeeType = employeeType,
)

//extract this out to a more common location for reuse
enum class ScreenState {
    LOADING,
    ERROR,
    EMPTY,
    DATA
}
