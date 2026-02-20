package it.posteitaliane.df_home.datasource

import android.content.Context
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.work.WorkManager
import it.posteitaliane.df_home.home.Constants.METTITI_IN_FILA
import it.posteitaliane.df_onebooking.datasource.AppuntamentoDao
import it.posteitaliane.df_onebooking.datasource.AppuntamentoLocalModel
import it.posteitaliane.df_sessionmanager.application_state.Status
import it.posteitaliane.df_utils.extensions.orZero
import it.posteitaliane.df_utils.model.home.Appointments
import it.posteitaliane.df_utils.model.home.Tickets
import it.posteitaliane.df_utils.model.migrazione.MigrationMettitiInFIlaModel
import it.posteitaliane.df_utils.model.migrazione.MigrazionePrenotaTicketModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class AppuntamentiRepository(
    private val dao: AppuntamentoDao,
    private val context: Context
 ) {
    private val ONETICKETING_TIMEZONE_DATE_FORMAT: String
        get() = if (Build.VERSION.SDK_INT >= 24) "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" else "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

    private val ONETICKETING_TIMEZONE_DATE_FORMAT_2: String
        get() = if (Build.VERSION.SDK_INT >= 24) "yyyy-MM-dd'T'HH:mm:ss.SSSXX" else "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

    fun getAppuntamenti(uid: String): Flow<List<AppuntamentoLocalModel>> {
        return dao.getAppuntamenti(uid).map {
            getAppuntamentiMatch(it)
        }
    }

    fun getAppuntamentiNoFlow(uid: String): List<AppuntamentoLocalModel> =
        getAppuntamentiMatch(dao.getAppuntamentiNoFlow(uid))

    @VisibleForTesting(PRIVATE)
    fun getAppuntamentiMatch(it: List<AppuntamentoLocalModel>): List<AppuntamentoLocalModel> {
        val (match, rest) = it.partition {
            //Add one hour to end time and check if it is not after now, if it is after now it is expired (delete only if is not mettiti in fila)
            (getDateFromString(it, it.endDateTime)?.time.orZero() + (1000 * 60 * 60)) > Calendar.getInstance().timeInMillis
        }

        dao.deleteAppuntamentiByIds(rest.filter { it.toAppuntamentoUiModel().ticketType != METTITI_IN_FILA }.map { it.id })
        return match
    }

    @VisibleForTesting(PRIVATE)
    fun getDateFromString(
        appuntamentoLocalModel: AppuntamentoLocalModel,
        s: String,
    ): Date? {
        val patternIn =
            if (appuntamentoLocalModel.qrCode == null && appuntamentoLocalModel.consulente == null) {
                SimpleDateFormat(ONETICKETING_TIMEZONE_DATE_FORMAT, Locale.getDefault())
            } else {
                SimpleDateFormat(ONETICKETING_TIMEZONE_DATE_FORMAT_2, Locale.getDefault())
            }
        return try {
            patternIn.parse(s)
        } catch (e: ParseException) {
            null
        }
    }

    fun saveMettitiInFilafromMigrazione(
        migrationMettitiInFIlaModel: MigrationMettitiInFIlaModel,
        userID: String,
    ) {
        dao.insertAppuntamento(
            AppuntamentoLocalModel.fromMigration(migrationMettitiInFIlaModel, userID)
        )
    }

    fun savePrenotaTicketfromMigrazione(
        migrazionePrenotaTicketModel: MigrazionePrenotaTicketModel,
        userID: String,
    ) {
        dao.insertAppuntamento(
            AppuntamentoLocalModel.fromMigration(migrazionePrenotaTicketModel, userID)
        )
    }

     fun savePrenotaTicketfromHomeService(tickets: List<Tickets>, userID: String) {
        tickets.forEach { ticket ->
            ticket.data?.let { qr ->
                if (dao.getAppuntamentoByQrCode(qr) == null)
                    dao.insertAppuntamentoReplace(
                        AppuntamentoLocalModel.fromHomeService(ticket, userID)
                    )
            }
        }
    }

    fun saveAppuntamentoConsulentefromHomeService(
        appointments: List<Appointments>?,
        userID: String,
    ) {
        appointments?.forEach { appointment ->
            appointment.id?.let { id ->
                val localRecord = dao.getAppuntamentoByIdConsulente(id)
                if (localRecord == null) {
                    dao.insertAppuntamento(
                        AppuntamentoLocalModel.fromHomeService(appointment, userID)
                    )
                } else {
                    /**
                     * Quando si creava un appuntamento consulente veniva settato il nome
                     * invece dello userid nel record del database (da giugno 2023...).
                     * Quando la home andava ad aggiornare gli appuntamenti locali il
                     * record esisteva già in quanto l'id combaciava.
                     * Con questo ELSE andiamo ad aggiornare il campo userid allineandolo
                     * al vero userid così sistemiamo record pregressi.
                     * - Da rimuovere quando la 5.6.43 verrà disattivata
                     */
                    dao.updateUserId(localRecord.id.toLong(), userID)
                }
            }
        }
    }


     fun cleanUpFromHome(
        loginStatus: Status,
        tickets: List<Tickets>?,
        appointments: List<Appointments>?,
        userID: String
    ) {
        if (loginStatus.isLogged()) {
            tickets?.let {
                val ticketsToKeep = tickets.map { ticket -> ticket.id.toString() }
                dao.getAllUserTickets(userID)
                    .filter { item -> item.ticketIdFromPtMode !in ticketsToKeep  && !item.qrCode.isNullOrEmpty()}
                    .run {
                        mapNotNull { it.reminderId }.forEach { UUID ->
                            cancelByUuid(context, UUID)
                        }
                        if (isNotEmpty())
                            dao.deleteAppuntamentiByIds(map { it.id })
                    }

            } ?: deleteAllTickets(userID)

            appointments?.let {
                val appointmentsToKeep = appointments.mapNotNull { appointment -> appointment.id }
                dao.getAllUserAppointments(userID)
                    .filter { item -> item.ticketIdFromPtMode !in appointmentsToKeep }.run {
                        mapNotNull { it.reminderId }.forEach { UUID ->
                            cancelByUuid(context, UUID)
                        }
                        if (isNotEmpty())
                            dao.deleteAppuntamentiByIds(map { it.id })
                    }

            } ?: deleteAllAppointments(userID)
        }
    }

    private fun deleteAllTickets(userID: String) {
        dao.getAllUserTickets(userID).mapNotNull { it.reminderId }.forEach { UUID ->
            cancelByUuid(context, UUID)
        }
        dao.deleteAllUserTickets(userID)
    }

    private fun deleteAllAppointments(userID: String) {
        dao.getAllUserAppointments(userID).mapNotNull { it.reminderId }.forEach { UUID ->
            cancelByUuid(context, UUID)
        }
        dao.deleteAllUserAppointments(userID)
    }

    private fun cancelByUuid(context: Context, uuid: UUID) = WorkManager
        .getInstance(context)
        .cancelWorkById(uuid)
}
