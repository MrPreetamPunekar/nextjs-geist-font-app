package com.example.bulksender.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bulksender.R
import com.example.bulksender.models.MessageLog
import com.google.android.material.chip.Chip

class LogAdapter(private val logs: List<MessageLog>) : 
    RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    private var filteredLogs = logs.toList()
    private var currentFilter = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(filteredLogs[position])
    }

    override fun getItemCount(): Int = filteredLogs.size

    fun filter(status: String) {
        currentFilter = status.lowercase()
        filteredLogs = when (currentFilter) {
            "success" -> logs.filter { it.isSuccess }
            "failed" -> logs.filter { it.isFailed }
            else -> logs.toList()
        }
        notifyDataSetChanged()
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipientNameText: TextView = itemView.findViewById(R.id.recipientNameText)
        private val recipientNumberText: TextView = itemView.findViewById(R.id.recipientNumberText)
        private val statusChip: Chip = itemView.findViewById(R.id.statusChip)
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val errorText: TextView = itemView.findViewById(R.id.errorText)
        private val attachmentsText: TextView = itemView.findViewById(R.id.attachmentsText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)

        fun bind(log: MessageLog) {
            // Set recipient info
            recipientNameText.text = log.recipientName.takeIf { it.isNotBlank() } 
                ?: log.recipientNumber
            recipientNumberText.text = log.recipientNumber
            recipientNumberText.visibility = if (log.recipientName.isNotBlank()) {
                View.VISIBLE
            } else View.GONE

            // Set status chip
            statusChip.apply {
                text = log.status.capitalize()
                setChipBackgroundColorResource(
                    when {
                        log.isSuccess -> R.color.status_success
                        log.isFailed -> R.color.status_failed
                        else -> R.color.status_pending
                    }
                )
            }

            // Set message
            messageText.text = log.shortMessage

            // Set error message if any
            errorText.apply {
                text = log.errorMessage
                visibility = if (log.errorMessage != null) View.VISIBLE else View.GONE
            }

            // Set attachments indicator
            attachmentsText.apply {
                if (log.hasAttachments) {
                    visibility = View.VISIBLE
                    text = "${log.attachments.size} attachment${if (log.attachments.size > 1) "s" else ""}"
                    setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_attachment,
                        0,
                        0,
                        0
                    )
                } else {
                    visibility = View.GONE
                }
            }

            // Set timestamp
            timestampText.text = log.formattedTimestamp
        }
    }

    private fun String.capitalize(): String {
        return this.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase() else it.toString() 
        }
    }
}
