package com.driverfinance.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.driverfinance.data.repository.CaptureRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

/**
 * WorkManager worker that sends a daily reminder at 23:00
 * to check Shopee history if there are trips without complete details.
 * Per F002 §6.3.2: reminder only fires if trips exist but details are incomplete.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val captureRepository: CaptureRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now().toString()
        val unlinkedTrips = captureRepository.getHistoryTripsWithoutLink(today)

        if (unlinkedTrips.isEmpty()) {
            return Result.success()
        }

        sendNotification(
            title = "\uD83D\uDCCB Jangan lupa cek riwayat Shopee",
            body = "hari ini supaya data lengkap!"
        )

        return Result.success()
    }

    private fun sendNotification(title: String, body: String) {
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pengingat Riwayat",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 2001
        const val WORK_NAME = "daily_history_reminder"
    }
}
