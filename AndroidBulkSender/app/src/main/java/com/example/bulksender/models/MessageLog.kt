package com.example.bulksender.models

import java.text.SimpleDateFormat
import java.util.*

data class MessageLog(
    val id: Long,
    val campaignId: Long,
    val recipientNumber: String,
    val recipientName: String,
    val message: String,
    val timestamp: Long,
    val status: String,
    val errorMessage: String? = null,
    val attachments: List<String> = emptyList()
) {
    companion object {
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())

        const val STATUS_SUCCESS = "success"
        const val STATUS_FAILED = "failed"
        const val STATUS_PENDING = "pending"
    }

    val formattedTimestamp: String
        get() = dateFormat.format(Date(timestamp))

    val isSuccess: Boolean
        get() = status == STATUS_SUCCESS

    val isFailed: Boolean
        get() = status == STATUS_FAILED

    val isPending: Boolean
        get() = status == STATUS_PENDING

    val statusColor: Int
        get() = when (status) {
            STATUS_SUCCESS -> android.graphics.Color.parseColor("#4CAF50") // Green
            STATUS_FAILED -> android.graphics.Color.parseColor("#F44336") // Red
            else -> android.graphics.Color.parseColor("#FFC107") // Yellow
        }

    val hasAttachments: Boolean
        get() = attachments.isNotEmpty()

    val shortMessage: String
        get() = if (message.length > 100) {
            "${message.substring(0, 97)}..."
        } else message
}
