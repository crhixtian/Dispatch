package com.gestion.gestionmantenimientosoftware.Model

import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("Code")
    val Code: Int,
    @SerializedName("Description")
    val Description: String,
    @SerializedName("Data")
    val Data: User
)
data class PickingDto(
    @SerializedName("Code")
    val Code: Int,
    @SerializedName("Description")
    val Description: String,
    @SerializedName("Data")
    val Data: Picking
)

data class MailDto(
    @SerializedName("Code")
    val Code: Int,
    @SerializedName("Description")
    val Description: String,
    @SerializedName("Data")
    val Data: Mail
)

data class StandardDto(
    @SerializedName("Code")
    val Code: Int,
    @SerializedName("Description")
    val Description: String
)