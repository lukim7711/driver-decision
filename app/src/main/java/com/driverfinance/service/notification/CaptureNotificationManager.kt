package com.driverfinance.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.driverfinance.MainActivity
import com.driverfinance.R

/**
 * Manages notification channels and notifications for auto capture.
 *
 * Channels:
 * - capture_persistent: Status bar tetap (IMPORTANCE_LOW, no sound)
 * - capture_popup: Pop-up singkat saat order ter-capture
 * - capture_reminder: Pengingat harian jam 23:00 (F002)
 */
class CaptureNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_PERSISTENT = "capture_persistent"
        const val CHANNEL_POPUP = "capture_popup"
        const val CHANNEL_REMINDER = "capture_reminder"

        const val ID_PERSISTENT = 1001
        const val ID_POPUP = 1002
        const val ID_REMINDER = 1003
    }

    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        val channels = listOf(
            NotificationChannel(
                CHANNEL_PERSISTENT,
                "Auto Capture Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Status auto capture yang sedang berjalan"
                setShowBadge(false)
            },
            NotificationChannel(
                CHANNEL_POPUP,
                "Notifikasi Capture",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi saat order berhasil ter-capture"
            },
            NotificationChannel(
                CHANNEL_REMINDER,
                "Pengingat Harian",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pengingat cek riwayat Shopee di akhir hari"
            }
        )
        manager.createNotificationChannels(channels)
    }

    fun buildPersistentNotification(orderCount: Int, tripCount: Int): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = if (orderCount > 0) {
            "\uD83D\uDCE6 Auto Capture aktif \u2014 $orderCount order hari ini ($tripCount trip)"
        } else {
            "\uD83D\uDCE6 Auto Capture aktif \u2014 Menunggu order..."
        }

        return NotificationCompat.Builder(context, CHANNEL_PERSISTENT)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Driver Finance")
            .setContentText(text)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun showPersistentNotification(orderCount: Int, tripCount: Int) {
        manager.notify(ID_PERSISTENT, buildPersistentNotification(orderCount, tripCount))
    }

    fun showOrderCapturedPopup(orderCode: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_POPUP)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Order Tersimpan")
            .setContentText("\uD83D\uDCE6 Order $orderCode tersimpan")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(ID_POPUP, notification)
    }

    fun showLearningProgress(sampleCount: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_POPUP)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Sedang Belajar")
            .setContentText("\uD83D\uDCDA Belajar pola Shopee... ($sampleCount contoh terkumpul)")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(ID_POPUP, notification)
    }

    fun showHistoryReminder() {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Cek Riwayat Shopee")
            .setContentText("\uD83D\uDCCB Jangan lupa cek riwayat Shopee hari ini supaya data lengkap!")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(ID_REMINDER, notification)
    }

    fun cancelAll() {
        manager.cancelAll()
    }
}
