package com.driverfinance.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.driverfinance.data.local.dao.ScreenSnapshotDao
import com.driverfinance.data.local.entity.ScreenSnapshotEntity
import com.driverfinance.service.accessibility.AccessibilityNodeExtractor
import com.driverfinance.service.accessibility.ScreenClassifier
import com.driverfinance.service.notification.CaptureNotificationManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Accessibility Service yang membaca UI tree Shopee Driver (com.shopee.speedy).
 * Menyimpan raw_text + node_tree_json ke screen_snapshots.
 *
 * Digunakan oleh F001 (Auto Capture Order Masuk) dan F002 (Capture Riwayat Rincian).
 *
 * Menggunakan Hilt EntryPoint karena AccessibilityService tidak support @AndroidEntryPoint.
 */
class CaptureAccessibilityService : AccessibilityService() {

    companion object {
        const val SHOPEE_DRIVER_PACKAGE = "com.shopee.speedy"
        private const val DEBOUNCE_MS = 1000L
        private val JAKARTA_ZONE = ZoneId.of("Asia/Jakarta")
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ServiceEntryPoint {
        fun screenSnapshotDao(): ScreenSnapshotDao
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var screenSnapshotDao: ScreenSnapshotDao
    private lateinit var notificationManager: CaptureNotificationManager

    private val nodeExtractor = AccessibilityNodeExtractor()
    private val screenClassifier = ScreenClassifier()

    private var lastCaptureTime = 0L
    private var lastContentHash = 0

    override fun onServiceConnected() {
        super.onServiceConnected()

        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            ServiceEntryPoint::class.java
        )
        screenSnapshotDao = entryPoint.screenSnapshotDao()

        notificationManager = CaptureNotificationManager(applicationContext)
        notificationManager.showPersistentNotification(0, 0)

        Timber.i("CaptureAccessibilityService connected — listening for %s", SHOPEE_DRIVER_PACKAGE)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val evt = event ?: return
        val packageName = evt.packageName?.toString() ?: return

        if (packageName != SHOPEE_DRIVER_PACKAGE) return

        when (evt.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                captureScreen()
            }
        }
    }

    private fun captureScreen() {
        val now = System.currentTimeMillis()
        if (now - lastCaptureTime < DEBOUNCE_MS) return
        lastCaptureTime = now

        val rootNode = rootInActiveWindow ?: return

        val rawText = nodeExtractor.extractAllText(rootNode)
        if (rawText.isBlank()) return

        val contentHash = rawText.hashCode()
        if (contentHash == lastContentHash) return
        lastContentHash = contentHash

        val nodeTreeJson = nodeExtractor.extractNodeTree(rootNode)
        val screenType = screenClassifier.classify(rawText)

        val timestamp = OffsetDateTime.now(JAKARTA_ZONE)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val snapshot = ScreenSnapshotEntity(
            id = UUID.randomUUID().toString(),
            screenType = screenType,
            imagePath = "",
            rawText = rawText,
            nodeTreeJson = nodeTreeJson,
            isProcessed = 0,
            capturedAt = timestamp,
            createdAt = timestamp
        )

        serviceScope.launch {
            try {
                screenSnapshotDao.insert(snapshot)
                Timber.d("Captured: type=%s len=%d", screenType, rawText.length)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save screen snapshot")
            }
        }
    }

    override fun onInterrupt() {
        Timber.w("CaptureAccessibilityService interrupted")
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
