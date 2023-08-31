package com.gestion.gestionmantenimientosoftware.Repository.Pdf

import android.content.Context
import com.gestion.gestionmantenimientosoftware.Model.PickingDto
import com.gestion.gestionmantenimientosoftware.Utils.OperationResult
import java.io.File

interface PdfRepository {

    fun createPDF(context: Context, picking: PickingDto): File
}