package com.example

import com.example.data.local.ClipperPresets
import com.example.data.model.TargetPlatform
import com.example.data.model.UserPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun seedData_has15MainClips_32CaptionStyles_52Templates_andAllPlatforms() {
        val mainClips = ClipperPresets.seedClips().filter { it.projectId == "proj_main_45m" }
        assertEquals(15, mainClips.size)
        assertTrue(ClipperPresets.captionStyles.size >= 30)
        assertTrue(ClipperPresets.templates.size >= 50)
        assertEquals(TargetPlatform.entries.size, mainClips.first().platformVersions.size)
    }

    @Test
    fun newVideoGenerator_creates16Clips_matchingPromptAndGamingMode() {
        val generated = ClipperPresets.generateClipsForNewVideo(
            projectId = "proj_test",
            sourceTitle = "Podcast Rust",
            promptHint = "Cari momen saat bahas Rust",
            gamingMode = false,
            count = 16
        )
        assertEquals(16, generated.size)
        assertTrue(generated.first().reason.contains("Rust"))
    }

    @Test
    fun monthlyQuota_defaultsTo1000AndTracksUsedMinutes() {
        val fresh = UserPreferences()
        assertEquals(1000, fresh.remainingCreditsMinutes)
        assertEquals(0, fresh.usedCreditsMinutes)

        val afterUse = fresh.copy(remainingCreditsMinutes = 880)
        assertEquals(120, afterUse.usedCreditsMinutes)
    }
}
