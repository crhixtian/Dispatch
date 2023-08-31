package com.gestion.app_despacho.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

data class User(
    val User: String,
    val FullName: String,
    val RolId: Int,
    val RolCode: String,
    val RolName: String
): Serializable

data class Picking(
    val idpicking: Int,
    val nbrpicking: String,
    val date_deliv: String,
    val date_mov_goods: String,
    val cod_sap_requ: String,
    val petitioner: String,
    val plate: String,
    val driver: String,
    val license: String,
    val Hour: String,
    val status: Int,
    val ruc: String,
    val observation: String,
    val pickingDet: List<PickingDetail>
): Serializable

data class PickingDetail(
    val nbrpicking: String,
    val cod_sap_material: String,
    val material: String,
    val dispatcher: String,
    val stevedores: List<Stevedores>,
    val quantity: Float,
    val weight: Float,
    val ton: Float,
    val type_load: String,
    val start: String,
    val end: String,
    val nrolote: String,
    val observation: String?
)

data class Stevedores(
    val nombre: String,
    val dni: String
)

data class Mail(
    val email: String
)


@Entity(tableName = "PickingHeader")
data class ClsPicking(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    val Id: Int,
    @ColumnInfo(name = "Picking")
    val Picking: String,
    @ColumnInfo(name = "date_deliv")
    val date_deliv: String,
    @ColumnInfo(name = "date_mov_goods")
    val date_mov_goods: String,
    @ColumnInfo(name = "cod_sap_requ")
    val cod_sap_requ: String,
    @ColumnInfo(name = "petitioner")
    val petitioner: String,
    @ColumnInfo(name = "plate")
    val plate: String,
    @ColumnInfo(name = "driver")
    val driver: String,
    @ColumnInfo(name = "license")
    val license: String,
    @ColumnInfo(name = "Hour")
    val Hour: String,
    @ColumnInfo(name = "status")
    val status: Int,
    @ColumnInfo(name = "observation")
    val observation: String?
): Serializable

@Entity(tableName = "PickingDetail", foreignKeys = [ForeignKey(
    entity = ClsPicking::class,
    parentColumns = arrayOf("Id"),
    childColumns = arrayOf("IdPicking"),
    onDelete = ForeignKey.CASCADE
)])
data class ClsPickingDetail(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    val Id: Int,
    @ColumnInfo(name = "IdPicking")
    val IdPicking: Int,
    @ColumnInfo(name = "cod_sap_material")
    val cod_sap_material: String,
    @ColumnInfo(name = "material")
    val material: String,
    @ColumnInfo(name = "quantity")
    val quantity: Float,
    @ColumnInfo(name = "weight")
    val weight: Float,
    @ColumnInfo(name = "ton")
    val ton: Float,
    @ColumnInfo(name = "type_load")
    val type_load: String,
    @ColumnInfo(name = "startDate")
    var startDate: String?,
    @ColumnInfo(name = "endDate")
    var endDate: String?,
    @ColumnInfo(name = "nbr_lot")
    var nbr_lot: String?,
    @ColumnInfo(name = "observation")
    var observation: String?
): Serializable

@Entity(tableName = "Stevedores", foreignKeys = [ForeignKey(
    entity = ClsPicking::class,
    parentColumns = arrayOf("Id"),
    childColumns = arrayOf("IdPicking"),
    onDelete = ForeignKey.CASCADE
)])
data class ClsStevedores(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name ="Id")
    val Id: Int,
    @ColumnInfo(name = "IdPicking")
    val IdPicking: Int,
    @ColumnInfo(name = "cod_sap_material")
    val cod_sap_material: String,
    @ColumnInfo(name = "type_load")
    val type_load: String,
    @ColumnInfo(name = "material")
    val material: String,
    @ColumnInfo(name = "nombre")
    var nombre: String,
    @ColumnInfo(name = "dni")
    var dni: String
): Serializable