package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class UserLinesResponse(
    @SerializedName("values")
    val values: List<UserLine>,
    @SerializedName("index")
    val index: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("last")
    val last: Boolean
)