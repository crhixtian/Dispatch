package com.gestion.gestionmantenimientosoftware.Repository.Mail

import android.content.Context
import com.gestion.gestionmantenimientosoftware.Model.PickingDto
import com.gestion.gestionmantenimientosoftware.Data.Remote.Api
import com.gestion.gestionmantenimientosoftware.Repository.Pdf.PdfRepository
import com.gestion.gestionmantenimientosoftware.Repository.Pdf.PdfRepositoryImp
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.SessionManager
import com.github.alvarosct02.mailing.MailSender
import io.reactivex.Completable


class MailRepository(
    val mailSender: MailSender = MailSender(
        Constants.HOST,
        Constants.SENDER,
        Constants.PASS
    )
) {

    var pdfRepository: PdfRepository = PdfRepositoryImp()

    fun execute(picking: PickingDto, context: Context): Completable {
        return Completable.defer {
            var file = pdfRepository.createPDF(context, picking)

            mailSender.addAttachment(file.name, file.absolutePath)

            mailSender.sendMail(
                "Entrega finalizada - Nro. Picking ${picking.Data.nbrpicking}",
                "",
                Constants.SENDER,
                SessionManager().getMails()
            )
            Completable.complete()
        }
    }

    suspend fun getMails(): Int {
        var entero = 0
        val response = Api.build().getMails()

        if (response.isSuccessful){
            response.body()?.let {
                if (response.body()?.Code == 200){
                    SessionManager().saveMails(it.Data.email)
                    entero = 1
                }
            }
        }
        return entero
    }

}