package com.gestion.despacho.utils

import com.gestion.despacho.BuildConfig


object Constants {

    const val APP_VERSION = "v${BuildConfig.VERSION_NAME}"
    const val OBSERVE = "OBSERVE"
    const val OBSERVE_MATERIAL = "OBSERVE MATERIAL"
    const val VALIDATE = "VALIDATE"
    const val LOG_OUT = "LOG_OUT"
    const val HOME = "HOME"
    const val MAILS = "MAILS"
    const val SENDER = "serviciosti@ladrillosgestion.com"
    const val PASS = "Serviciosti93$"
    const val HOST = "smtp.gmail.com"
    const val HOUR_VERIFY = "HOUR_VERIFY"
    const val DATE_VERIFY = "DATE_VERIFY"
    const val ROL = "ROL_ID"
    const val STATUS_SESSION = "STATUS_SESSION"
    const val RESPONSE = "RESPONSE"
    const val NAME_PREFERENCES = "PREFS_APP"
    const val PREFS_API = "PREFS_API"
    const val PICKING = "PICKING"
    const val PICKING_FRAGMENT = "PICKING_FRAGMENT"
    const val RESPONSE_SUCCESSFULLY = "SUCCESS"
    const val STEVEDOR_SUCCES = "Estibador agregado correctamente"
    const val RESPONSE_ERROR = "OCURRIÓ UN ERROR"
    const val RESPONSE_SUCCESSFULLY_DETAILS = "Se obtuvo el detalle del picking correctamente"
    const val CANCELED_PICKING = "PICKING ANULADO"
    const val CANCELED_PICKING_DESC = "El picking que intenta consultar se encuentra anulado"
    const val ERROR_NO_STEVEDORES = "Debe añadir estibadores para finalizar la carga"
    const val PREFS_STATUS = "STATUS"
    const val ERROR_DNI = "El número de dni debe tener 8 dígitos"
    const val ERROR_FIELDS = "Complete todos los campos"
    const val DATE_FORMAT_FULL = "dd/MM/yyyy"
    const val HOUR_FORMAT = "HH:mm:ss"
    const val HOUR_FORMAT_SHORT = "HH:mm"
    const val USER = "Usuario"
    const val USER_LOGIN = "OBJ_USUARIO"
    const val MODULE_CODE = "M0002"

    //PDF
    const val TITTLE_FORMAT = "FORMATO"
    const val CTRL_DESPACHO = "CONTROL DE DESPACHO"
    const val TITTLE_WAREHOUSE = "ALMACEN DE PRODUCTOS TERMINADOS"
    const val DYF = "DYF-F-005"
    const val VERSION = "Versión: "
    const val NRO_VERSION = "01"
    const val DATE = "Fecha: "
    const val REV = "Rev: "
    const val VAL_REV = "JL"
    const val APROB = "Aprob: "
    const val VAL_APROB = "GGA"
    const val DATE_MAY = "FECHA: "
    const val HOUR = "HORA: "
    const val PLATE = "PLACA: "
    const val RAZ_SOCIAL = "RAZÓN SOCIAL: "
    const val RUC = "RUC: "
    const val DRIVER = "CONDUCTOR: "
    const val LICENSE = "LICENCIA: "
    const val TITTLE_DETAIL_CHARGE = "DETALLE DE CARGA DE PRODUCTOS: "
    const val NRO_PICKING = "NRO DE PICKING: "
    const val PRODUCT = "PRODUCTO"
    const val QUANTITY = "CANTIDAD"
    const val WEIGHT = "PESO"
    const val UNIT_W = "(KG)"
    const val TON = "TON."
    const val TYPE = "TIPO"
    const val CHARGE = "CARGA"
    const val START = "INICIO"
    const val END = "FIN"
    const val OBSERVATION = "OBSERVACIÓN"
    const val ONE = "1"
    const val TWO = "2"
    const val THREE = "3"
    const val FOUR = "4"
    const val FIVE = "5"
    const val SIX = "6"
    const val SEVEN = "7"
    const val STEVEDORES = "ESTIBADORES RESPONSABLES"
    const val NAMES = "NOMBRES"
    const val DNI = "DNI"
    const val CONTROLLER = "CONTROLADOR / DESPACHADOR RESPONSABLE"
    const val SIGNATURE = "FIRMA"
    const val OBSERVATIONS = "OBSERVACIONES"
    const val CTRL_TOWER = "TORRE DE CONTROL"
    const val DISPATCH = "DESPACHO"
    const val VIGILANCE = "VIGILANCIA"
    const val HOUR_EXIT = "HORA DE SALIDA DE VEHÍCULO: "
    const val TRANSPORT_SIGNATURE = "FIRMA DEL TRANSPORTE"
    const val TEXT_LINE_1 = "Los ladrillos de arcilla destinados para uso en albañilería estructural y No estructural (No decorativo) donde la apariencia externa tales como; Identaciones menores"
    const val TEXT_LINE_2 = "o grietas superficiales inherentes al método usual de fabricación, o los astillamientos resultantes de los métodos habituales de manipulación en el envío y despacho,"
    const val TEXT_LINE_3 = "no serán consideradas causa de rechazo."
    const val TEXT_LINE_4 = "los lotes de todo producto ofrecido poseen más del 90% de eficacia de conformidad del producto."
}
