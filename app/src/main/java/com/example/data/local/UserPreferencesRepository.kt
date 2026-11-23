package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.BrandKitConfig
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "clipper_adha_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val CREDITS_MINUTES = intPreferencesKey("credits_minutes")
        val QUOTA_RESET_MONTH = stringPreferencesKey("quota_reset_month")
        val QUOTA_TIER_VERSION = intPreferencesKey("quota_tier_version")
        val DATA_SAVER = booleanPreferencesKey("data_saver")
        val GAMING_DEFAULT = booleanPreferencesKey("gaming_default")
        val AUTO_COMPRESS = booleanPreferencesKey("auto_compress_80mb")
        val PWA_INSTALLED = booleanPreferencesKey("pwa_installed")
        val SIMULATE_LOW_RAM = booleanPreferencesKey("simulate_low_ram")

        val BRAND_NAME = stringPreferencesKey("brand_name")
        val BRAND_INITIALS = stringPreferencesKey("brand_initials")
        val BRAND_PRIMARY_HEX = longPreferencesKey("brand_primary_hex")
        val BRAND_ACCENT_HEX = longPreferencesKey("brand_accent_hex")
        val BRAND_FONT = stringPreferencesKey("brand_font")
        val BRAND_INTRO_ON = booleanPreferencesKey("brand_intro_on")
        val BRAND_INTRO_TEXT = stringPreferencesKey("brand_intro_text")
        val BRAND_OUTRO_ON = booleanPreferencesKey("brand_outro_on")
        val BRAND_OUTRO_TEXT = stringPreferencesKey("brand_outro_text")
        val WATERMARK_ON = booleanPreferencesKey("watermark_on")
        val ACTIVE_TEMPLATE_ID = stringPreferencesKey("active_template_id")
    }

    private fun currentYearMonthKey(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return "$year-${month.toString().padStart(2, '0')}"
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val tierVersion = prefs[Keys.QUOTA_TIER_VERSION] ?: 1
        val savedCredits = prefs[Keys.CREDITS_MINUTES] ?: UserPreferences.MONTHLY_QUOTA_MINUTES
        val effectiveCredits = if (tierVersion < 2) {
            UserPreferences.MONTHLY_QUOTA_MINUTES
        } else {
            savedCredits.coerceIn(0, UserPreferences.MONTHLY_QUOTA_MINUTES)
        }
        UserPreferences(
            remainingCreditsMinutes = effectiveCredits,
            dataSaverMode = prefs[Keys.DATA_SAVER] ?: true,
            gamingModeDefault = prefs[Keys.GAMING_DEFAULT] ?: false,
            autoCompressOver80Mb = prefs[Keys.AUTO_COMPRESS] ?: true,
            pwaInstalled = prefs[Keys.PWA_INSTALLED] ?: false,
            simulateLowRamWarning = prefs[Keys.SIMULATE_LOW_RAM] ?: false
        )
    }

    val brandKit: Flow<BrandKitConfig> = context.dataStore.data.map { prefs ->
        BrandKitConfig(
            brandName = prefs[Keys.BRAND_NAME] ?: "Adha Creator Studio",
            logoInitials = prefs[Keys.BRAND_INITIALS] ?: "CA",
            primaryColorHex = prefs[Keys.BRAND_PRIMARY_HEX] ?: 0xFF8B5CF6,
            accentColorHex = prefs[Keys.BRAND_ACCENT_HEX] ?: 0xFFF59E0B,
            fontName = prefs[Keys.BRAND_FONT] ?: "Outfit Bold",
            introEnabled = prefs[Keys.BRAND_INTRO_ON] ?: true,
            introText = prefs[Keys.BRAND_INTRO_TEXT] ?: "CLIPPER ADHA PRO",
            outroEnabled = prefs[Keys.BRAND_OUTRO_ON] ?: true,
            outroText = prefs[Keys.BRAND_OUTRO_TEXT] ?: "Follow & Subscribe!",
            watermarkEnabled = prefs[Keys.WATERMARK_ON] ?: false,
            activeTemplateId = prefs[Keys.ACTIVE_TEMPLATE_ID] ?: "tpl_podcast_01"
        )
    }

    suspend fun checkAndResetMonthlyQuotaIfNeeded(): Boolean {
        val currentMonth = currentYearMonthKey()
        var didReset = false
        context.dataStore.edit { prefs ->
            val savedMonth = prefs[Keys.QUOTA_RESET_MONTH]
            val tierVersion = prefs[Keys.QUOTA_TIER_VERSION] ?: 1
            if (savedMonth == null || savedMonth != currentMonth || tierVersion < 2) {
                prefs[Keys.CREDITS_MINUTES] = UserPreferences.MONTHLY_QUOTA_MINUTES
                prefs[Keys.QUOTA_RESET_MONTH] = currentMonth
                prefs[Keys.QUOTA_TIER_VERSION] = 2
                if (savedMonth != null && savedMonth != currentMonth) {
                    didReset = true
                }
            }
        }
        return didReset
    }

    suspend fun deductCredits(minutes: Int): Int {
        var updatedRemaining = 0
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.CREDITS_MINUTES] ?: UserPreferences.MONTHLY_QUOTA_MINUTES
            updatedRemaining = (current - minutes).coerceAtLeast(0)
            prefs[Keys.CREDITS_MINUTES] = updatedRemaining
            prefs[Keys.QUOTA_TIER_VERSION] = 2
            if (prefs[Keys.QUOTA_RESET_MONTH] == null) {
                prefs[Keys.QUOTA_RESET_MONTH] = currentYearMonthKey()
            }
        }
        return updatedRemaining
    }

    suspend fun resetMonthlyCredits() {
        context.dataStore.edit { prefs ->
            prefs[Keys.CREDITS_MINUTES] = UserPreferences.MONTHLY_QUOTA_MINUTES
            prefs[Keys.QUOTA_RESET_MONTH] = currentYearMonthKey()
            prefs[Keys.QUOTA_TIER_VERSION] = 2
        }
    }

    suspend fun setDataSaver(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DATA_SAVER] = enabled }
    }

    suspend fun setGamingModeDefault(enabled: Boolean) {
        context.dataStore.edit { it[Keys.GAMING_DEFAULT] = enabled }
    }

    suspend fun setAutoCompressOver80Mb(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_COMPRESS] = enabled }
    }

    suspend fun setPwaInstalled(installed: Boolean) {
        context.dataStore.edit { it[Keys.PWA_INSTALLED] = installed }
    }

    suspend fun setSimulateLowRam(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SIMULATE_LOW_RAM] = enabled }
    }

    suspend fun updateBrandKit(config: BrandKitConfig) {
        context.dataStore.edit { prefs ->
            prefs[Keys.BRAND_NAME] = config.brandName
            prefs[Keys.BRAND_INITIALS] = config.logoInitials
            prefs[Keys.BRAND_PRIMARY_HEX] = config.primaryColorHex
            prefs[Keys.BRAND_ACCENT_HEX] = config.accentColorHex
            prefs[Keys.BRAND_FONT] = config.fontName
            prefs[Keys.BRAND_INTRO_ON] = config.introEnabled
            prefs[Keys.BRAND_INTRO_TEXT] = config.introText
            prefs[Keys.BRAND_OUTRO_ON] = config.outroEnabled
            prefs[Keys.BRAND_OUTRO_TEXT] = config.outroText
            prefs[Keys.WATERMARK_ON] = config.watermarkEnabled
            prefs[Keys.ACTIVE_TEMPLATE_ID] = config.activeTemplateId
        }
    }
}
