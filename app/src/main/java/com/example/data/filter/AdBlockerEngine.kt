package com.example.data.filter

class AdBlockerEngine {
    private val adDomains = hashSetOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "adservice.google.com",
        "criteo.com",
        "taboola.com",
        "outbrain.com",
        "adnxs.com",
        "popads.net",
        "adroll.com",
        "scorecardresearch.com",
        "rubiconproject.com",
        "amazon-adsystem.com",
        "smartadserver.com"
    )

    private val trackerDomains = hashSetOf(
        "google-analytics.com",
        "analytics.google.com",
        "hotjar.com",
        "connect.facebook.net",
        "facebook.com/tr",
        "clarity.ms",
        "segment.io",
        "mixpanel.com"
    )

    private val whitelist = hashSetOf<String>()

    fun extractHost(url: String): String? {
        return try {
            val cleaned = if (url.contains("://")) url.substringAfter("://") else url
            val hostAndPort = cleaned.substringBefore("/")
            hostAndPort.substringBefore(":").lowercase().trim()
        } catch (_: Exception) {
            null
        }
    }

    fun isBlocked(url: String): Pair<Boolean, String> {
        val host = extractHost(url) ?: return Pair(false, "")

        if (whitelist.contains(host)) return Pair(false, "")

        for (ad in adDomains) {
            if (host == ad || host.endsWith(".$ad")) {
                return Pair(true, "ad")
            }
        }

        for (tracker in trackerDomains) {
            if (host == tracker || host.endsWith(".$tracker")) {
                return Pair(true, "tracker")
            }
        }

        return Pair(false, "")
    }

    fun addWhitelist(domain: String) {
        whitelist.add(domain.lowercase().trim())
    }

    fun removeWhitelist(domain: String) {
        whitelist.remove(domain.lowercase().trim())
    }
}
