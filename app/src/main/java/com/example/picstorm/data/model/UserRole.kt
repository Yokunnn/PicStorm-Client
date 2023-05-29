package com.example.picstorm.data.model

import com.example.picstorm.domain.model.enums.UserRole
import com.google.gson.annotations.SerializedName

data class UserRole(
    @SerializedName("userId")
    val id: Long,
    @SerializedName("newRole")
    val newRole: UserRole
)
