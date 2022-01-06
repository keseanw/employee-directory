package com.block.employeedirectory.data.model

import com.google.gson.annotations.SerializedName

data class EmployeeDTO (
    @SerializedName("employees") val employees: List<Employee>,
)

data class Employee (
    @SerializedName("uuid") val uuid: String?,
    @SerializedName("full_name") val name: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("email_address") val email: String?,
    @SerializedName("biography") val bio: String?,
    @SerializedName("photo_url_small") val photoSmall: String?,
    @SerializedName("photo_url_large") val photoLarge: String?,
    @SerializedName("team") val team: String?,
    @SerializedName("employee_type") val employeeType: EmployeeType?,
)

enum class EmployeeType {
    FULL_TIME, PART_TIME, CONTRACTOR
}
