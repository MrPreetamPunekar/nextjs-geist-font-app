package com.example.bulksender.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bulksender.R
import com.example.bulksender.models.Campaign
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.*

class CampaignAdapter(
    private val campaigns: List<Campaign>,
    private val onCampaignClick: (Campaign) -> Unit
) : RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder>() {

    private val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_campaign, parent, false)
        return CampaignViewHolder(view)
    }

    override fun onBindViewHolder(holder: CampaignViewHolder, position: Int) {
        val campaign = campaigns[position]
        holder.bind(campaign)
    }

    override fun getItemCount(): Int = campaigns.size

    inner class CampaignViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.campaignNameText)
        private val statusChip: Chip = itemView.findViewById(R.id.statusChip)
        private val totalContactsText: TextView = itemView.findViewById(R.id.totalContactsText)
        private val successFailedText: TextView = itemView.findViewById(R.id.successFailedText)
        private val progressIndicator: LinearProgressIndicator = itemView.findViewById(R.id.progressIndicator)
        private val scheduledTimeText: TextView = itemView.findViewById(R.id.scheduledTimeText)

        fun bind(campaign: Campaign) {
            nameText.text = campaign.name
            
            // Set status chip style and text
            statusChip.apply {
                text = campaign.status
                setChipBackgroundColorResource(
                    when (campaign.status) {
                        "Running" -> R.color.status_running
                        "Completed" -> R.color.status_completed
                        "Scheduled" -> R.color.status_scheduled
                        else -> R.color.status_default
                    }
                )
            }

            totalContactsText.text = campaign.totalContacts.toString()
            successFailedText.text = "${campaign.successCount}/${campaign.failureCount}"

            // Set progress
            progressIndicator.progress = (campaign.progress * 100).toInt()

            // Show scheduled time if available
            campaign.scheduledTime?.let { scheduledTime ->
                scheduledTimeText.apply {
                    visibility = View.VISIBLE
                    text = "Scheduled for: ${dateFormat.format(Date(scheduledTime))}"
                }
            } ?: run {
                scheduledTimeText.visibility = View.GONE
            }

            // Set click listener
            itemView.setOnClickListener { onCampaignClick(campaign) }
        }
    }
}
