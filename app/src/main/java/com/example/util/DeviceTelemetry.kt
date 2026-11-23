package com.example.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.provider.OpenableColumns
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.MainActivity
import com.example.R

data class DeviceHealthInfo(
    val freeStorageGb: String,
    val availableRamMb: Long,
    val totalRamMb: Long,
    val isLowRam: Boolean
)

data class PickedVideoMeta(
    val uriString: String,
    val fileName: String,
    val sizeMb: Int
)

object DeviceTelemetry {

    fun readDeviceHealth(context: Context): DeviceHealthInfo {
        val stat = StatFs(Environment.getDataDirectory().path)
        val freeBytes = stat.availableBytes
        val freeGb = freeBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
        val freeStorageStr = String.format("%.1f GB", freeGb)

        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memInfo)
        val availMb = memInfo.availMem / (1024L * 1024L)
        val totalMb = memInfo.totalMem / (1024L * 1024L)
        val isLow = memInfo.lowMemory || availMb < 900L

        return DeviceHealthInfo(
            freeStorageGb = freeStorageStr,
            availableRamMb = availMb,
            totalRamMb = totalMb,
            isLowRam = isLow
        )
    }

    fun readPickedVideoMeta(context: Context, uri: Uri): PickedVideoMeta {
        var name = "video_pilihan.mp4"
        var sizeBytes = 48L * 1024L * 1024L
        runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIdx >= 0) {
                        name = cursor.getString(nameIdx) ?: name
                    }
                    if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) {
                        sizeBytes = cursor.getLong(sizeIdx)
                    }
                }
            }
        }
        val sizeMb = ((sizeBytes / (1024L * 1024L)).toInt()).coerceAtLeast(1)
        return PickedVideoMeta(
            uriString = uri.toString(),
            fileName = name,
            sizeMb = sizeMb
        )
    }

    fun openShareSheet(context: Context, title: String, caption: String, hashtags: String) {
        val text = buildString {
            appendLine(title)
            appendLine()
            appendLine(caption)
            appendLine()
            append(hashtags)
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(sendIntent, "Bagikan Clip ke Media Sosial")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun requestPinHomeShortcut(context: Context): Boolean {
        return runCatching {
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_MAIN
                }
                val shortcut = ShortcutInfoCompat.Builder(context, "clipper_adha_shortcut")
                    .setShortLabel("Clipper Adha")
                    .setLongLabel("Clipper Adha Pro - AI Video Clipper")
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))
                    .setIntent(launchIntent)
                    .build()
                ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
            } else {
                false
            }
        }.getOrDefault(false)
    }

    val pwaManifestJsonPreview: String = """
        {
          "id": "/",
          "name": "Clipper Adha",
          "short_name": "Clipper Adha",
          "description": "Clipper Adha - Ubah video panjang jadi 10 clip viral otomatis, gratis 1000 menit per bulan",
          "start_url": "/",
          "scope": "/",
          "display": "standalone",
          "background_color": "#000000",
          "theme_color": "#FFD700",
          "orientation": "portrait",
          "icons": [
            {
              "src": "icon-192.png",
              "sizes": "192x192",
              "type": "image/png",
              "purpose": "any"
            },
            {
              "src": "icon-192.png",
              "sizes": "192x192",
              "type": "image/png",
              "purpose": "maskable"
            },
            {
              "src": "icon-512.png",
              "sizes": "512x512",
              "type": "image/png",
              "purpose": "any"
            },
            {
              "src": "icon-512.png",
              "sizes": "512x512",
              "type": "image/png",
              "purpose": "maskable"
            }
          ]
        }
    """.trimIndent()

    val pwaMetaTagsPreview: String = """
        <meta name="theme-color" content="#000000" />
        <link rel="manifest" href="manifest.json">
        <link rel="apple-touch-icon" href="icon-192.png" />
    """.trimIndent()

    val pwaServiceWorkerRoutes: String = """
        Bundle Offline 1 Folder (service-worker.js + capacitor.config.json):
        • ./index.html, ./styles.css, ./app.js
        • ./manifest.json, ./capacitor.config.json
        • ./icon-192.png, ./icon-512.png
    """.trimIndent()
}
