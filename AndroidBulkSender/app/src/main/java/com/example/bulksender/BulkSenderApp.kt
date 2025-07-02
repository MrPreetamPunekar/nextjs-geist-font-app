package com.example.bulksender

import android.app.Application
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.bulksender.managers.CampaignManager
import com.example.bulksender.services.WhatsAppAccessibilityService

class BulkSenderApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize app-wide components
        initializeComponents()

        // Check accessibility service
        checkAccessibilityService()
    }

    private fun initializeComponents() {
        // Force light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize CampaignManager
        campaignManager = CampaignManager.getInstance(this)
    }

    private fun checkAccessibilityService() {
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(
                this,
                getString(R.string.enable_accessibility),
                Toast.LENGTH_LONG
            ).show()

            // Open accessibility settings
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED,
            0
        )

        if (accessibilityEnabled == 1) {
            val serviceString = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )

            serviceString?.let {
                return it.contains(packageName + "/" + WhatsAppAccessibilityService::class.java.name)
            }
        }
        return false
    }

    companion object {
        private lateinit var instance: BulkSenderApp
        private lateinit var campaignManager: CampaignManager

        fun getInstance(): BulkSenderApp = instance
        fun getCampaignManager(): CampaignManager = campaignManager

        // Utility function to check if WhatsApp is installed
        fun isWhatsAppInstalled(packageManager: android.content.pm.PackageManager): Boolean {
            return try {
                packageManager.getPackageInfo("com.whatsapp", 0)
                true
            } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
                false
            }
        }

        // Utility function to check if a number is registered on WhatsApp
        fun isNumberRegistered(number: String): Boolean {
            // TODO: Implement WhatsApp number validation
            // This would typically involve trying to find the contact in WhatsApp
            // through the Accessibility Service
            return true
        }
    }
}
