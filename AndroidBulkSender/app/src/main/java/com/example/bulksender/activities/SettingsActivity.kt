package com.example.bulksender.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.bulksender.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {

    private lateinit var minDelayInput: TextInputEditText
    private lateinit var maxDelayInput: TextInputEditText
    private lateinit var typingSimulationSwitch: SwitchMaterial
    private lateinit var hourlyLimitInput: TextInputEditText
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupToolbar()
        initializeViews()
        setupPreferences()
        loadSettings()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeViews() {
        minDelayInput = findViewById(R.id.minDelayInput)
        maxDelayInput = findViewById(R.id.maxDelayInput)
        typingSimulationSwitch = findViewById(R.id.typingSimulationSwitch)
        hourlyLimitInput = findViewById(R.id.hourlyLimitInput)
    }

    private fun setupPreferences() {
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    private fun loadSettings() {
        // Load delay settings
        val minDelay = prefs.getLong(PREF_MIN_DELAY, DEFAULT_MIN_DELAY) / 1000 // Convert to seconds
        val maxDelay = prefs.getLong(PREF_MAX_DELAY, DEFAULT_MAX_DELAY) / 1000
        minDelayInput.setText(minDelay.toString())
        maxDelayInput.setText(maxDelay.toString())

        // Load typing simulation setting
        typingSimulationSwitch.isChecked = prefs.getBoolean(PREF_SIMULATE_TYPING, true)

        // Load hourly limit
        val hourlyLimit = prefs.getInt(PREF_HOURLY_LIMIT, 0)
        hourlyLimitInput.setText(if (hourlyLimit > 0) hourlyLimit.toString() else "")
    }

    private fun setupListeners() {
        // Delay input validation
        val delayWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateAndSaveDelays()
            }
        }

        minDelayInput.addTextChangedListener(delayWatcher)
        maxDelayInput.addTextChangedListener(delayWatcher)

        // Typing simulation switch
        typingSimulationSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PREF_SIMULATE_TYPING, isChecked).apply()
        }

        // Hourly limit input
        hourlyLimitInput.addTextChangedListener {
            val limit = it.toString().toIntOrNull() ?: 0
            prefs.edit().putInt(PREF_HOURLY_LIMIT, limit).apply()
        }
    }

    private fun validateAndSaveDelays() {
        val minDelay = minDelayInput.text.toString().toLongOrNull() ?: DEFAULT_MIN_DELAY / 1000
        val maxDelay = maxDelayInput.text.toString().toLongOrNull() ?: DEFAULT_MAX_DELAY / 1000

        if (minDelay > maxDelay) {
            minDelayInput.error = "Minimum delay cannot be greater than maximum delay"
            return
        }

        if (minDelay < 1) {
            minDelayInput.error = "Minimum delay must be at least 1 second"
            return
        }

        if (maxDelay > 60) {
            maxDelayInput.error = "Maximum delay cannot exceed 60 seconds"
            return
        }

        // Clear any error states
        minDelayInput.error = null
        maxDelayInput.error = null

        // Save values (convert to milliseconds)
        prefs.edit()
            .putLong(PREF_MIN_DELAY, minDelay * 1000)
            .putLong(PREF_MAX_DELAY, maxDelay * 1000)
            .apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val PREFS_NAME = "campaign_settings"
        private const val PREF_MIN_DELAY = "min_delay"
        private const val PREF_MAX_DELAY = "max_delay"
        private const val PREF_SIMULATE_TYPING = "simulate_typing"
        private const val PREF_HOURLY_LIMIT = "hourly_limit"

        private const val DEFAULT_MIN_DELAY = 5000L // 5 seconds
        private const val DEFAULT_MAX_DELAY = 10000L // 10 seconds
    }
}
