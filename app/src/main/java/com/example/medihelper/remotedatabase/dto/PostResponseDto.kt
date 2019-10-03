package com.example.medihelper.remotedatabase.dto

import com.google.gson.annotations.SerializedName

data class PostResponseDto(
    @SerializedName(value = "localId")
    val localId: Int,

    @SerializedName(value = "remoteId")
    val remoteId: Long
)