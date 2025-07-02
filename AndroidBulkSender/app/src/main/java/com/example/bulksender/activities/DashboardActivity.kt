package com.example.bulksender.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.bulksender.R
import com.example.bulksender.adapters.DashboardPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var toolbar: Toolbar
    private lateinit var fabNewCampaign: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCurrentNumber: TextView
    private lateinit var tvProgress: TextView
    private lateinit var activeCampaignCard: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initializeViews()
        setupToolbar()
        setupViewPager()
        setupFab()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        fabNewCampaign = findViewById(R.id.fabNewCampaign)
        progressBar = findViewById(R.id.progressBar)
        tvCurrentNumber = findViewById(R.id.tvCurrentNumber)
        tvProgress = findViewById(R.id.tvProgress)
        activeCampaignCard = findViewById(R.id.activeCampaignCard)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun setupViewPager() {
        val pagerAdapter = DashboardPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Campaigns"
                1 -> "Logs"
                else -> "Settings"
            }
        }.attach()
    }

    private fun setupFab() {
        fabNewCampaign.setOnClickListener {
            startNewCampaign()
        }
    }

    private fun startNewCampaign() {
        // Start MessageComposerActivity to create a new campaign
        startActivity(Intent(this, MessageComposerActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Method to update campaign progress
    fun updateProgress(currentNumber: String, progress: Int, total: Int) {
        activeCampaignCard.visibility = View.VISIBLE
        tvCurrentNumber.text = "Processing: $currentNumber"
        progressBar.progress = progress
        progressBar.max = total
        tvProgress.text = "Progress: $progress/$total"
    }

    // Method to hide progress when campaign is complete
    fun hideCampaignProgress() {
        activeCampaignCard.visibility = View.GONE
    }

    companion object {
        private const val TAG = "DashboardActivity"
    }
}
