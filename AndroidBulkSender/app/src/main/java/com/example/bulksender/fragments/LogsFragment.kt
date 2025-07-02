package com.example.bulksender.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bulksender.R
import com.example.bulksender.adapters.LogAdapter
import com.example.bulksender.models.MessageLog
import com.google.android.material.chip.ChipGroup
import java.util.*

class LogsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var logAdapter: LogAdapter
    private lateinit var filterChipGroup: ChipGroup
    private val logs = mutableListOf<MessageLog>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        setupFilters()
        loadLogs()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.logsRecyclerView)
        filterChipGroup = view.findViewById(R.id.filterChipGroup)
    }

    private fun setupRecyclerView() {
        logAdapter = LogAdapter(logs)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = logAdapter
        }
    }

    private fun setupFilters() {
        filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.allChip -> showAllLogs()
                R.id.successChip -> showSuccessLogs()
                R.id.failedChip -> showFailedLogs()
            }
        }
    }

    private fun loadLogs() {
        // TODO: Load logs from database
        // For now, add sample data
        logs.clear()
        logs.addAll(getSampleLogs())
        logAdapter.notifyDataSetChanged()
    }

    private fun showAllLogs() {
        logAdapter.filter("")
    }

    private fun showSuccessLogs() {
        logAdapter.filter("success")
    }

    private fun showFailedLogs() {
        logAdapter.filter("failed")
    }

    private fun getSampleLogs(): List<MessageLog> {
        return listOf(
            MessageLog(
                id = 1,
                campaignId = 1,
                recipientNumber = "+1234567890",
                recipientName = "John Doe",
                message = "Hello John! How are you?",
                timestamp = System.currentTimeMillis() - 3600000,
                status = "success"
            ),
            MessageLog(
                id = 2,
                campaignId = 1,
                recipientNumber = "+1987654321",
                recipientName = "Jane Smith",
                message = "Hi Jane! Special offer for you!",
                timestamp = System.currentTimeMillis() - 7200000,
                status = "failed",
                errorMessage = "Number not registered on WhatsApp"
            )
        )
    }

    fun addLog(log: MessageLog) {
        logs.add(0, log)
        logAdapter.notifyItemInserted(0)
        recyclerView.smoothScrollToPosition(0)
    }

    companion object {
        fun newInstance() = LogsFragment()
    }
}
