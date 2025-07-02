package com.example.bulksender.managers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bulksender.models.Campaign
import com.example.bulksender.models.Contact
import com.example.bulksender.services.WhatsAppAccessibilityService
import com.example.bulksender.utils.DelayUtil
import com.example.bulksender.utils.SpintaxParser
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class CampaignManager private constructor(private val context: Context) {

    private val campaignScope = CoroutineScope(Dispatchers.Main + Job())
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentCampaign = MutableLiveData<Campaign?>()
    val currentCampaign: LiveData<Campaign?> = _currentCampaign

    private val _progress = MutableLiveData<CampaignProgress>()
    val progress: LiveData<CampaignProgress> = _progress

    private var isRunning = false
    private var isPaused = false
    private var currentIndex = 0
    private var successCount = 0
    private var failureCount = 0

    fun startCampaign(campaign: Campaign, contacts: List<Contact>) {
        if (isRunning) return

        isRunning = true
        isPaused = false
        currentIndex = 0
        successCount = 0
        failureCount = 0

        _currentCampaign.value = campaign
        
        campaignScope.launch {
            processCampaign(campaign, contacts)
        }
    }

    private suspend fun processCampaign(campaign: Campaign, contacts: List<Contact>) {
        val validContacts = contacts.filter { it.isSelected && it.isValid }
        
        withContext(Dispatchers.Main) {
            _progress.value = CampaignProgress(
                currentIndex = currentIndex,
                total = validContacts.size,
                successCount = successCount,
                failureCount = failureCount,
                currentContact = null,
                status = CampaignStatus.STARTING
            )
        }

        // Get settings
        val minDelay = prefs.getLong(PREF_MIN_DELAY, 5000)
        val maxDelay = prefs.getLong(PREF_MAX_DELAY, 10000)
        val simulateTyping = prefs.getBoolean(PREF_SIMULATE_TYPING, true)

        for (contact in validContacts) {
            if (!isRunning) break
            while (isPaused) delay(1000)

            try {
                // Update progress
                withContext(Dispatchers.Main) {
                    _progress.value = _progress.value?.copy(
                        currentIndex = currentIndex,
                        currentContact = contact,
                        status = CampaignStatus.PROCESSING
                    )
                }

                // Process message template
                val variables = contact.getVariableValues(
                    SpintaxParser.extractVariables(campaign.message ?: "")
                )
                val processedMessage = SpintaxParser.parse(campaign.message ?: "", variables)

                // Send message
                sendMessage(contact, processedMessage, simulateTyping)

                // Success
                successCount++
                currentIndex++

                // Random delay between messages
                delay(DelayUtil.getRandomDelay(minDelay, maxDelay))

            } catch (e: Exception) {
                failureCount++
                currentIndex++
            }
        }

        // Campaign completed
        withContext(Dispatchers.Main) {
            _progress.value = _progress.value?.copy(
                currentIndex = currentIndex,
                successCount = successCount,
                failureCount = failureCount,
                status = CampaignStatus.COMPLETED
            )
            isRunning = false
            _currentCampaign.value = null
        }
    }

    private suspend fun sendMessage(contact: Contact, message: String, simulateTyping: Boolean) {
        withContext(Dispatchers.Main) {
            val intent = Intent(context, WhatsAppAccessibilityService::class.java).apply {
                action = WhatsAppAccessibilityService.ACTION_SEND_MESSAGE
                putExtra(WhatsAppAccessibilityService.EXTRA_PHONE_NUMBER, contact.phoneNumber)
                putExtra(WhatsAppAccessibilityService.EXTRA_MESSAGE, message)
                putExtra(WhatsAppAccessibilityService.EXTRA_SIMULATE_TYPING, simulateTyping)
            }
            context.startService(intent)
        }

        // Wait for message to be sent
        var attempts = 0
        while (attempts < MAX_ATTEMPTS) {
            delay(1000)
            if (messageWasSent()) break
            attempts++
        }

        if (attempts >= MAX_ATTEMPTS) {
            throw Exception("Message sending timeout")
        }
    }

    private fun messageWasSent(): Boolean {
        // TODO: Implement message sent verification
        return true
    }

    fun pauseCampaign() {
        isPaused = true
        _progress.value = _progress.value?.copy(status = CampaignStatus.PAUSED)
    }

    fun resumeCampaign() {
        isPaused = false
        _progress.value = _progress.value?.copy(status = CampaignStatus.PROCESSING)
    }

    fun stopCampaign() {
        isRunning = false
        _progress.value = _progress.value?.copy(status = CampaignStatus.STOPPED)
        _currentCampaign.value = null
    }

    data class CampaignProgress(
        val currentIndex: Int,
        val total: Int,
        val successCount: Int,
        val failureCount: Int,
        val currentContact: Contact?,
        val status: CampaignStatus
    )

    enum class CampaignStatus {
        STARTING,
        PROCESSING,
        PAUSED,
        STOPPED,
        COMPLETED
    }

    companion object {
        private const val TAG = "CampaignManager"
        private const val PREFS_NAME = "campaign_settings"
        private const val PREF_MIN_DELAY = "min_delay"
        private const val PREF_MAX_DELAY = "max_delay"
        private const val PREF_SIMULATE_TYPING = "simulate_typing"
        private const val MAX_ATTEMPTS = 30

        @Volatile
        private var instance: CampaignManager? = null

        fun getInstance(context: Context): CampaignManager {
            return instance ?: synchronized(this) {
                instance ?: CampaignManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
