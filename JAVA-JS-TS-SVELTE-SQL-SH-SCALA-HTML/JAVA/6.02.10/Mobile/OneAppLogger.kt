package it.posteitaliane.df_utils.logger

import android.content.Context
import android.util.Log
import it.posteitaliane.df_utils.ENABLE_LOG
import kotlinx.coroutines.runBlocking
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class OneAppLogger(val context: Context, level: Level = Level.INFO) : Logger(level) {
    private val fileDir = "OneAppEventsLogs"
    private val shouldILog = ENABLE_LOG

    init {
        kotlin.runCatching {
            val folderPath = "${context.getExternalFilesDir(null)}/$fileDir"
            val logDir = File(context.filesDir, folderPath)
            if (!logDir.exists()) {
                logDir.mkdirs()
            } else {
                val files = logDir.listFiles()
                files?.forEach {
                    deleteOlderFiles(it)
                }
            }
        }
    }

    override fun display(level: Level, msg: MESSAGE) = Unit


    fun d(msg: String, extra: String = "") {
        if (shouldILog) {
            Log.d(TAG, msg)
            writeLog("| debug | $msg $extra")
        }
    }

    fun e(msg: String, extra: String = "") {
        if (shouldILog) {
            Log.e(TAG, msg)
            writeLog("| error | $msg $extra")
        }
    }

    fun i(msg: String, extra: String = ""){
        if (shouldILog) {
            Log.i(TAG, msg)
            writeLog("| info | $extra | $msg")
        }
    }

    private fun writeLog(message: String) {
            runBlocking {
                kotlin.runCatching {
                    val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss", Locale.ITALIAN)
                    val sdfM = SimpleDateFormat("dd_M_yyyy", Locale.ITALIAN)
                    val currentDate = sdf.format(Date())
                    val fileName = sdfM.format(Date())
                    val logDir = File(context.filesDir, fileDir)
                    if (!logDir.exists()) {
                        logDir.mkdirs()
                    }
                    val file = File(logDir, "$fileName.log")
                    val writer = FileWriter(file, true)
                    writer.write("\n$currentDate $message \n\n")
                    writer.flush()
                    writer.close()
                }
            }
    }

    fun getLogFile(day: Date = Date()): File {
        val sdfM = SimpleDateFormat("dd_M_yyyy", Locale.ITALIAN)
        val fileName = sdfM.format(day)
        val logDir = File(context.filesDir, fileDir)
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        val file = File(logDir, "$fileName.log")
        if (!file.exists()) file.createNewFile()
        return file
    }

    private fun deleteOlderFiles(file: File) {
        val diff: Long = Date().time - file.lastModified()
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        if (days >= 30) file.delete()
    }

    companion object {
        const val TAG = "[OneApp]"
        const val zipFileName = "logs.zip"
    }
}
