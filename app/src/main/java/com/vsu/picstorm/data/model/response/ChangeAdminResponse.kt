package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class ChangeRoleResponse (
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("newRole")
    val newRole: String
)