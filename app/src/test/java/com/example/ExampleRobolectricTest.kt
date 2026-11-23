package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ClipperPresets
import com.example.data.model.TargetPlatform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app_name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Clipper Adha", appName)
  }

  @Test
  fun `seed data includes 15 main clips 32 caption styles and 52 templates`() {
    val mainClips = ClipperPresets.seedClips().filter { it.projectId == "proj_main_45m" }
    assertEquals(15, mainClips.size)
    assertTrue(ClipperPresets.captionStyles.size >= 30)
    assertTrue(ClipperPresets.templates.size >= 50)
    assertEquals(TargetPlatform.entries.size, mainClips.first().platformVersions.size)
  }
}
