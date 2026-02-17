package com.driverfinance.service.accessibility

/**
 * Classifies Shopee Driver screen type based on keyword matching.
 *
 * Screen types (ref: ARCHITECTURE.md):
 * - ORDER_DETAIL   → F001 capture (alamat, kode order, dll)
 * - HISTORY_LIST   → F002 capture (list riwayat pesanan)
 * - HISTORY_DETAIL  → F002 capture (rincian pesanan)
 * - UNKNOWN        → Halaman lain / belum dikenali
 *
 * Saat ini menggunakan heuristic keywords.
 * Akan di-enhance dengan parsing_patterns dari Discovery Mode saat F001/F002 build.
 */
class ScreenClassifier {

    companion object {
        const val TYPE_ORDER_DETAIL = "ORDER_DETAIL"
        const val TYPE_HISTORY_LIST = "HISTORY_LIST"
        const val TYPE_HISTORY_DETAIL = "HISTORY_DETAIL"
        const val TYPE_UNKNOWN = "UNKNOWN"

        private val ORDER_DETAIL_KEYWORDS = listOf(
            "alamat pickup", "alamat pengiriman", "detail pesanan",
            "no. pesanan", "berat paket"
        )

        private val HISTORY_LIST_KEYWORDS = listOf(
            "riwayat pesanan", "pesanan gabungan"
        )

        private val HISTORY_DETAIL_KEYWORDS = listOf(
            "rincian pesanan", "biaya pengantaran", "total pendapatan",
            "kompensasi bayar tunai"
        )
    }

    fun classify(rawText: String): String {
        val text = rawText.lowercase()

        val scores = mapOf(
            TYPE_ORDER_DETAIL to ORDER_DETAIL_KEYWORDS.count { text.contains(it) },
            TYPE_HISTORY_DETAIL to HISTORY_DETAIL_KEYWORDS.count { text.contains(it) },
            TYPE_HISTORY_LIST to HISTORY_LIST_KEYWORDS.count { text.contains(it) }
        )

        val best = scores.maxByOrNull { it.value } ?: return TYPE_UNKNOWN
        return if (best.value > 0) best.key else TYPE_UNKNOWN
    }
}
