package com.vsu.picstorm.data.model

import com.vsu.picstorm.domain.model.enums.UserRole
import com.google.gson.annotations.SerializedName

data class UserRole(
    @SerializedName("userId")
    val id: Long,
    @SerializedName("newRole")
    val newRole: UserRole
)
