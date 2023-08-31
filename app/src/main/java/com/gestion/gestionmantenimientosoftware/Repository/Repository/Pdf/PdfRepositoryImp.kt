package com.gestion.gestionmantenimientosoftware.Repository.Pdf

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.gestion.gestionmantenimientosoftware.Application.App
import com.gestion.gestionmantenimientosoftware.Model.PickingDto
import com.gestion.gestionmantenimientosoftware.R
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.Format.format
import com.gestion.gestionmantenimientosoftware.Utils.SessionManager
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

class PdfRepositoryImp : PdfRepository {

    private val pageWidth = 1200f

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Throws(FileNotFoundException::class)
    override fun createPDF(context: Context, picking: PickingDto): File {
        val objPicking = picking.Data

        val pdfDocument = PdfDocument()
        val paint = Paint()

        val infoPage = PageInfo.Builder(1200, 2010, 1).create()
        val page = pdfDocument.startPage(infoPage)
        val canvas = page.canvas

        //Cabecera
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(20f, 20f, pageWidth - 20f, 160f, paint)
        canvas.drawLine(250f, 20f, 250f, 160f, paint)
        canvas.drawLine(800f, 20f, 800f, 160f, paint)
        canvas.drawLine(250f, 65f, pageWidth - 20f, 65f, paint)

        //Logo
        val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
        val scaledbmp = Bitmap.createScaledBitmap(bmp, 1000, 500, false)
        val rectangle = Rect(30, 70, 245, 110)
        canvas.drawBitmap(scaledbmp, null, rectangle, paint)

        //paint.textAlign = Paint.Align.CENTER
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.textSize = 20f
        canvas.drawText(Constants.TITTLE_FORMAT, 480f, 55f, paint)
        canvas.drawText(Constants.CTRL_DESPACHO, 440f, 105f, paint)
        canvas.drawText(Constants.TITTLE_WAREHOUSE, 340f, 135f, paint)
        canvas.drawText(Constants.DYF, 950f, 55f, paint)

        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(Constants.VERSION, 810f, 90f, paint)
        canvas.drawText(Constants.NRO_VERSION, pageWidth - 50f, 90f, paint)
        canvas.drawText(Constants.DATE, 810f, 110f, paint)
        canvas.drawText(getDate(), pageWidth - 130f, 110f, paint)
        canvas.drawText(Constants.REV, 810f, 130f, paint)
        canvas.drawText(Constants.VAL_REV, pageWidth - 50f, 130f, paint)
        canvas.drawText(Constants.APROB, 810f, 150f, paint)
        canvas.drawText(Constants.VAL_APROB, pageWidth - 70f, 150f, paint)

        //Sección info
        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(Constants.DATE_MAY, 20f, 190f, paint)
        canvas.drawText(objPicking.date_deliv, 250f, 190f, paint)
        canvas.drawText(Constants.HOUR, 430f, 190f, paint)
        canvas.drawText(objPicking.Hour, 500f, 190f, paint)
        canvas.drawText(Constants.PLATE, 20f, 240f, paint)
        canvas.drawText(objPicking.plate, 250f, 240f, paint)
        canvas.drawText(Constants.RAZ_SOCIAL, 20f, 290f, paint)
        canvas.drawText(objPicking.petitioner, 250f, 290f, paint)
        canvas.drawText(Constants.RUC, 755f, 290f, paint)
        canvas.drawText(objPicking.ruc, pageWidth - 135f, 290f, paint)
        canvas.drawText(Constants.DRIVER, 20f, 340f, paint)
        canvas.drawText(objPicking.driver, 250f, 340f, paint)
        canvas.drawText(Constants.LICENSE, 710f, 340f, paint)
        canvas.drawText(objPicking.license, pageWidth - 115f, 340f, paint)
        canvas.drawText(Constants.TITTLE_DETAIL_CHARGE, 20f, 390f, paint)

        paint.style = Paint.Style.FILL_AND_STROKE
        paint.textSize = 25f
        canvas.drawText(Constants.NRO_PICKING, 650f, 190f, paint)
        canvas.drawText(objPicking.nbrpicking, pageWidth - 155f, 190f, paint)

        //Detalle de carga
        //Lineas horizontales
        canvas.drawLine(20f, 460f, pageWidth - 20f, 460f, paint)
        paint.color = ContextCompat.getColor(context, R.color.gray)
        canvas.drawLine(20f, 500f, pageWidth - 20f, 500f, paint)
        canvas.drawLine(20f, 540f, pageWidth - 20f, 540f, paint)
        canvas.drawLine(20f, 580f, pageWidth - 20f, 580f, paint)
        canvas.drawLine(20f, 620f, pageWidth - 20f, 620f, paint)
        canvas.drawLine(20f, 660f, pageWidth - 20f, 660f, paint)
        canvas.drawLine(20f, 700f, pageWidth - 20f, 700f, paint)
        //Lineas verticales
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(20f, 400f, pageWidth - 20f, 740f, paint)
        canvas.drawLine(60f, 460f, 60f, 740f, paint)
        canvas.drawLine(400f, 400f, 400f, 740f, paint)
        canvas.drawLine(505f, 400f, 505f, 740f, paint)
        canvas.drawLine(580f, 400f, 580f, 740f, paint)
        canvas.drawLine(650f, 400f, 650f, 740f, paint)
        canvas.drawLine(750f, 400f, 750f, 740f, paint)
        canvas.drawLine(850f, 400f, 850f, 740f, paint)
        canvas.drawLine(950f, 400f, 950f, 740f, paint)

        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(Constants.PRODUCT, 150f, 440f, paint)
        canvas.drawText(Constants.QUANTITY, 405f, 440f, paint)
        canvas.drawText(Constants.WEIGHT, 517f, 430f, paint)
        canvas.drawText(Constants.UNIT_W, 522f, 450f, paint)
        canvas.drawText(Constants.TON, 595f, 440f, paint)
        canvas.drawText(Constants.TYPE, 675f, 430f, paint)
        canvas.drawText(Constants.CHARGE, 665f, 450f, paint)
        canvas.drawText(Constants.HOUR, 774f, 430f, paint)
        canvas.drawText(Constants.START, 772f, 450f, paint)
        canvas.drawText(Constants.HOUR, 872f, 430f, paint)
        canvas.drawText(Constants.END, 882f, 450f, paint)
        canvas.drawText(Constants.OBSERVATION, 1000f, 440f, paint)
        canvas.drawText(Constants.ONE, 35f, 485f, paint)
        canvas.drawText(Constants.TWO, 35f, 525f, paint)
        canvas.drawText(Constants.THREE, 35f, 565f, paint)
        canvas.drawText(Constants.FOUR, 35f, 605f, paint)
        canvas.drawText(Constants.FIVE, 35f, 645f, paint)
        canvas.drawText(Constants.SIX, 35f, 685f, paint)
        canvas.drawText(Constants.SEVEN, 35f, 725f, paint)

        //Llenando productos
        var y = 490f
        var temp = 0f

        objPicking.pickingDet.forEach {
            val hStart = it.start.split(" ")[1]
            val hEnd = it.end.split(" ")[1]
            canvas.drawText(it.material, 70f, y + temp, paint)
            canvas.drawText(it.quantity.toString(), 405f, y + temp, paint)
            canvas.drawText(it.weight.toString(), 515f, y + temp, paint)
            canvas.drawText(it.ton.toString(), 590f, y + temp, paint)
            canvas.drawText(it.type_load, 665f, y + temp, paint)
            canvas.drawText(hStart, 770f, y + temp, paint)
            canvas.drawText(hEnd, 870f, y + temp, paint)
            it.observation?.let {obs -> canvas.drawText(obs, 960f, y+temp, paint) }
            temp += 40f
        }


        //Estibadores
        //Lineas horizontales
        paint.color = ContextCompat.getColor(context, R.color.gray)
        canvas.drawLine(20f, 830f, pageWidth - 20f, 830f, paint)
        canvas.drawLine(20f, 870f, pageWidth - 20f, 870f, paint)
        canvas.drawLine(20f, 910f, pageWidth - 20f, 910f, paint)
        canvas.drawLine(20f, 950f, pageWidth - 20f, 950f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = ContextCompat.getColor(context, R.color.black)
        //Cuadro estibadores
        canvas.drawRect(20f, 790f, pageWidth - 20f, 990f, paint)
        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(Constants.STEVEDORES, 20f, 780f, paint)
        //Lineas verticales
        canvas.drawLine(60f, 790f, 60f, 990f, paint)
        canvas.drawLine(580f, 790f, 580f, 990f, paint)
        canvas.drawLine(750f, 790f, 750f, 990f, paint)

        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(Constants.NAMES, 250f, 820f, paint)
        canvas.drawText(Constants.DNI, 650f, 820f, paint)
        canvas.drawText(Constants.SIGNATURE, 940f, 820f, paint)
        canvas.drawText(Constants.ONE, 35f, 855f, paint)
        canvas.drawText(Constants.TWO, 35f, 895f, paint)
        canvas.drawText(Constants.THREE, 35f, 935f, paint)
        canvas.drawText(Constants.FOUR, 35f, 975f, paint)

        //Llenando estibadores
        temp = 0f
        y = 860f
        objPicking.pickingDet.forEach {
            it.stevedores.forEach { stevedor ->
                canvas.drawText(stevedor.nombre, 70f, y + temp, paint)
                canvas.drawText(stevedor.dni, 620f, y + temp, paint)
                temp += 40f
            }
        }

        //Controlador responsable
        canvas.drawText(Constants.CONTROLLER, 20f, 1030f, paint)
        //Cuadro responsable
        //Lineas horizontales
        paint.color = ContextCompat.getColor(context, R.color.gray)
        canvas.drawLine(20f, 1080f, pageWidth - 20f, 1080f, paint)
        canvas.drawLine(20f, 1120f, pageWidth - 20f, 1120f, paint)
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(20f, 1040f, pageWidth - 20f, 1240f, paint)
        canvas.drawLine(20f, 1160f, pageWidth - 20f, 1160f, paint)
        //Lineas verticales
        canvas.drawLine(60f, 1040f, 60f, 1160f, paint)
        canvas.drawLine(580f, 1040f, 580f, 1160f, paint)
        canvas.drawLine(750f, 1040f, 750f, 1160f, paint)

        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(Constants.NAMES, 250f, 1070f, paint)
        canvas.drawText(Constants.DNI, 650f, 1070f, paint)
        canvas.drawText(Constants.SIGNATURE, 940f, 1070f, paint)
        canvas.drawText(Constants.ONE, 35f, 1105f, paint)
        canvas.drawText(Constants.TWO, 35f, 1145f, paint)
        paint.flags = Paint.UNDERLINE_TEXT_FLAG
        canvas.drawText(Constants.OBSERVATIONS, 30f, 1180f, paint)
        paint.flags = Paint.ANTI_ALIAS_FLAG
        //Llenando controladores
        temp = 0f
        y = 1110f
        objPicking.pickingDet.forEach {
            canvas.drawText(it.dispatcher, 70f, y + temp, paint)
            temp += 40f
        }

        //Torre de control
        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(Constants.CTRL_TOWER, 20f, 1280f, paint)
        //Lineas horizontales
        paint.color = ContextCompat.getColor(context, R.color.gray)
        canvas.drawLine(20f, 1330f, pageWidth - 20f, 1330f, paint)
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(20f, 1290f, pageWidth - 20f, 1450f, paint)
        canvas.drawLine(20f, 1370f, pageWidth - 20f, 1370f, paint)

        //Lineas verticales
        canvas.drawLine(60f, 1290f, 60f, 1370f, paint)
        canvas.drawLine(540f, 1290f, 540f, 1370f, paint)
        canvas.drawLine(650f, 1290f, 650f, 1370f, paint)
        canvas.drawLine(760f, 1290f, 760f, 1370f, paint)

        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(Constants.NAMES, 250f, 1320f, paint)
        canvas.drawText(Constants.HOUR, 570f, 1320f, paint)
        //canvas.drawText("18:50", 550f, 1390f, paint)
        canvas.drawText(Constants.DNI, 690f, 1320f, paint)
        //canvas.drawText("71406050", 660f, 1390f, paint)
        canvas.drawText(Constants.SIGNATURE, 940f, 1320f, paint)
        canvas.drawText(Constants.ONE, 35f, 1355f, paint)
        paint.flags = Paint.UNDERLINE_TEXT_FLAG
        canvas.drawText(Constants.OBSERVATIONS, 30f, 1390f, paint)
        paint.flags = Paint.ANTI_ALIAS_FLAG

        //Llenando torre de control
        SessionManager().getUser()?.let { canvas.drawText(it, 70f, 1360f, paint) }
        canvas.drawText(getHour(), 570f, 1360f, paint)
        canvas.drawText(objPicking.observation, 30f, 1420f, paint)

        //Firmas
        //Linea horizontal
        paint.color = ContextCompat.getColor(context, R.color.gray)
        canvas.drawLine(20f, 1540f, pageWidth - 20f, 1540f, paint)
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(20f, 1500f, pageWidth - 20f, 1750f, paint)
        //Linea vertical
        canvas.drawLine(pageWidth / 2, 1500f, pageWidth / 2, 1750f, paint)


        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(Constants.DISPATCH, 250f, 1530f, paint)
        canvas.drawText(Constants.VIGILANCE, 850f, 1530f, paint)
        canvas.drawText("", 200f, 1740f, paint)
        canvas.drawText(Constants.HOUR_EXIT, (pageWidth / 2) + 10f, 1740f, paint)

        //Firma del transporte
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(20f, 1800f, pageWidth - 20f, 1970f, paint)
        canvas.drawLine(20f, 1870f, pageWidth - 20f, 1870f, paint)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.textSize = 18f
        canvas.drawText(Constants.TRANSPORT_SIGNATURE, 25f, 1820f, paint)
        paint.style = Paint.Style.FILL
        paint.textSize = 15f
        canvas.drawText(Constants.TEXT_LINE_1, 25f, 1890f, paint)
        canvas.drawText(Constants.TEXT_LINE_2, 25f, 1905f, paint)
        canvas.drawText(Constants.TEXT_LINE_3, 25f, 1920f, paint)
        canvas.drawText(Constants.TEXT_LINE_4, 25f, 1960f, paint)

        pdfDocument.finishPage(page)
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()
        val file = File(path, "${objPicking.nbrpicking}.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        pdfDocument.close()
        return file
    }

    /*private fun getPicking(nbrPicking: String): Picking {
        return try{

        }catch (e: Exception){
            null
        }
    }*/
    private fun getDate(): String {
        val now = Calendar.getInstance()
        return now.format(Constants.DATE_FORMAT_FULL)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHour(): String {
        val time = Calendar.getInstance()
        return time.format(Constants.HOUR_FORMAT_SHORT)
    }
}