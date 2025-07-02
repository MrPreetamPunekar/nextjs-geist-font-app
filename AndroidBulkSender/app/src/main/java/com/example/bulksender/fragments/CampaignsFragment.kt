package com.example.bulksender.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bulksender.R
import com.example.bulksender.models.Campaign
import com.example.bulksender.adapters.CampaignAdapter
import com.google.android.material.snackbar.Snackbar

class CampaignsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var campaignAdapter: CampaignAdapter
    private val campaigns = mutableListOf<Campaign>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_campaigns, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        loadCampaigns()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.campaignsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        campaignAdapter = CampaignAdapter(campaigns) { campaign ->
            // Handle campaign click
            showCampaignDetails(campaign)
        }
        
        recyclerView.adapter = campaignAdapter
    }

    private fun loadCampaigns() {
        // TODO: Load campaigns from local database
        // For now, add sample data
        campaigns.clear()
        campaigns.addAll(getSampleCampaigns())
        campaignAdapter.notifyDataSetChanged()
    }

    private fun showCampaignDetails(campaign: Campaign) {
        // Show campaign details in a bottom sheet or new activity
        Snackbar.make(
            requireView(),
            "Campaign: ${campaign.name}",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun getSampleCampaigns(): List<Campaign> {
        return listOf(
            Campaign(
                id = 1,
                name = "Welcome Message",
                status = "Completed",
                totalContacts = 100,
                successCount = 95,
                failureCount = 5,
                createdAt = System.currentTimeMillis()
            ),
            Campaign(
                id = 2,
                name = "Promotional Campaign",
                status = "Scheduled",
                totalContacts = 50,
                successCount = 0,
                failureCount = 0,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    companion object {
        fun newInstance() = CampaignsFragment()
    }
}
