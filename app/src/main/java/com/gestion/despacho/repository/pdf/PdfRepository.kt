package com.gestion.despacho.repository.pdf

import android.content.Context
import com.gestion.despacho.model.PickingDto
import java.io.File

interface PdfRepository {

    fun createPDF(context: Context, picking: PickingDto): File
}