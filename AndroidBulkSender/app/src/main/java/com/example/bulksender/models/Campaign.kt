package com.example.bulksender.models

data class Campaign(
    val id: Long,
    val name: String,
    val status: String,
    val totalContacts: Int,
    val successCount: Int,
    val failureCount: Int,
    val createdAt: Long,
    val scheduledTime: Long? = null,
    val message: String? = null,
    val attachmentPaths: List<String> = emptyList()
) {
    val progress: Float
        get() = if (totalContacts > 0) {
            (successCount + failureCount).toFloat() / totalContacts
        } else 0f

    val isCompleted: Boolean
        get() = status == "Completed"

    val isScheduled: Boolean
        get() = status == "Scheduled"

    val isRunning: Boolean
        get() = status == "Running"
}
