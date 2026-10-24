package com.example.data.model

import java.util.UUID

data class Tab(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "Nouvel onglet",
    var url: String = "madasearch://home",
    var isLoading: Boolean = false,
    var progress: Int = 0,
    var canGoBack: Boolean = false,
    var canGoForward: Boolean = false,
    val isPrivate: Boolean = false,
    val workspaceId: String = "default",
    var securityStatus: SecurityStatus = SecurityStatus.INTERNAL,
    var groupName: String? = null
)

enum class SecurityStatus {
    INTERNAL,
    SECURE_HTTPS,
    INSECURE_HTTP,
    WARNING
}

data class Workspace(
    val id: String,
    val name: String,
    val colorHex: Long,
    val iconName: String
)

data class Bookmark(
    val id: Long = 0,
    val title: String,
    val url: String,
    val folder: String = "Favoris",
    val addedAt: Long = System.currentTimeMillis()
)

data class HistoryItem(
    val id: Long = 0,
    val title: String,
    val url: String,
    val visitedAt: Long = System.currentTimeMillis()
)

data class DataSaverStats(
    val bytesTransferred: Long = 0L,
    val estimatedBytesSaved: Long = 0L,
    val adsBlocked: Long = 0L,
    val trackersBlocked: Long = 0L,
    val imagesBlocked: Long = 0L
) {
    val totalSavedFormatted: String
        get() {
            val kb = estimatedBytesSaved / 1024
            return if (kb > 1024) {
                String.format("%.1f Mo", kb / 1024.0)
            } else {
                "$kb Ko"
            }
        }
}

data class QuickLink(
    val title: String,
    val url: String,
    val iconEmoji: String,
    val tag: String
)
