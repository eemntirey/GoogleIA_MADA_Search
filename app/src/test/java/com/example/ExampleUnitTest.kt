package com.example

import com.example.data.filter.AdBlockerEngine
import com.example.data.model.DataSaverStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun adBlocker_blocksDoubleclickAndAnalytics() {
        val blocker = AdBlockerEngine()
        val (adBlocked, categoryAd) = blocker.isBlocked("https://adservice.google.com/ads.js")
        assertTrue(adBlocked)
        assertEquals("ad", categoryAd)

        val (trackerBlocked, categoryTracker) = blocker.isBlocked("https://google-analytics.com/analytics.js")
        assertTrue(trackerBlocked)
        assertEquals("tracker", categoryTracker)

        val (cleanBlocked, _) = blocker.isBlocked("https://orange.mg/offre")
        assertFalse(cleanBlocked)
    }

    @Test
    fun adBlocker_whitelistBypassesFilter() {
        val blocker = AdBlockerEngine()
        blocker.addWhitelist("connect.facebook.net")

        val (blocked, _) = blocker.isBlocked("https://connect.facebook.net/sdk.js")
        assertFalse(blocked)
    }

    @Test
    fun dataSaverStats_formatsCorrectly() {
        val stats1 = DataSaverStats(estimatedBytesSaved = 512 * 1024L)
        assertEquals("512 Ko", stats1.totalSavedFormatted)

        val stats2 = DataSaverStats(estimatedBytesSaved = 2500 * 1024L)
        assertEquals("2.4 Mo", stats2.totalSavedFormatted)
    }
}
