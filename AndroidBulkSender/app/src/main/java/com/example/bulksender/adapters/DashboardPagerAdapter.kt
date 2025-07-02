package com.example.bulksender.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bulksender.fragments.CampaignsFragment
import com.example.bulksender.fragments.LogsFragment
import com.example.bulksender.fragments.SettingsFragment

class DashboardPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CampaignsFragment()
            1 -> LogsFragment()
            2 -> SettingsFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
