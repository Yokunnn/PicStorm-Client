package com.example.picstorm.di.model

import com.example.picstorm.model.enums.UserRole
import com.google.gson.annotations.SerializedName

data class UserRole(
    @SerializedName("userId")
    val id: Long,
    @SerializedName("newRole")
    val newRole: UserRole
)
