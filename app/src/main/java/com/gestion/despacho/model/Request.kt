package com.gestion.despacho.model

data class LoginRequest(
    val User: String,
    val Password: String,
    val CodigoModulo: String
)

data class PickingLoadRequest(
    val nbrpicking: String,
    val cod_sap_material: String,
    val Fec_Attent: String,
    val state: Int,
    val hour: String,
    val nbr_lot: String,
    val user: String
)

data class StevedoresRequest(
    val nbrpicking: String,
    val cod_sap_material: String,
    val type_load: String,
    val stevedores: Stevedores
)

data class StatusPickingRequest(
    val nbrpicking: String,
    val validated: Int,
    val observation: String?,
    val user: String
)

data class ObserveMaterialRequest(
    val picking: String,
    val cod_material: String,
    val observation: String
)