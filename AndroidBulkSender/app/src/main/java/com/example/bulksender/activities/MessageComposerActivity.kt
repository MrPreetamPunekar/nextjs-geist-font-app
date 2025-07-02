package com.example.bulksender.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bulksender.R
import com.example.bulksender.adapters.AttachmentAdapter
import com.example.bulksender.models.Attachment
import com.example.bulksender.utils.SpintaxParser
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class MessageComposerActivity : AppCompatActivity() {

    private lateinit var campaignNameInput: TextInputEditText
    private lateinit var messageTemplateInput: TextInputEditText
    private lateinit var attachmentsRecyclerView: RecyclerView
    private lateinit var addAttachmentButton: MaterialButton
    private lateinit var scheduleSwitch: SwitchMaterial
    private lateinit var scheduleContainer: View
    private lateinit var datePickerButton: MaterialButton
    private lateinit var timePickerButton: MaterialButton
    private lateinit var startCampaignButton: ExtendedFloatingActionButton

    private lateinit var attachmentAdapter: AttachmentAdapter
    private val attachments = mutableListOf<Attachment>()
    
    private var scheduledDateTime: Calendar? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleAttachment(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_composer)

        initializeViews()
        setupToolbar()
        setupAttachments()
        setupScheduling()
        setupStartCampaign()
    }

    private fun initializeViews() {
        campaignNameInput = findViewById(R.id.campaignNameInput)
        messageTemplateInput = findViewById(R.id.messageTemplateInput)
        attachmentsRecyclerView = findViewById(R.id.attachmentsRecyclerView)
        addAttachmentButton = findViewById(R.id.addAttachmentButton)
        scheduleSwitch = findViewById(R.id.scheduleSwitch)
        scheduleContainer = findViewById(R.id.scheduleContainer)
        datePickerButton = findViewById(R.id.datePickerButton)
        timePickerButton = findViewById(R.id.timePickerButton)
        startCampaignButton = findViewById(R.id.startCampaignButton)
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupAttachments() {
        attachmentAdapter = AttachmentAdapter(attachments) { position ->
            attachments.removeAt(position)
            attachmentAdapter.notifyItemRemoved(position)
        }

        attachmentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MessageComposerActivity)
            adapter = attachmentAdapter
        }

        addAttachmentButton.setOnClickListener {
            getContent.launch("*/*")
        }
    }

    private fun setupScheduling() {
        scheduleSwitch.setOnCheckedChangeListener { _, isChecked ->
            scheduleContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) scheduledDateTime = null
        }

        datePickerButton.setOnClickListener { showDatePicker() }
        timePickerButton.setOnClickListener { showTimePicker() }
    }

    private fun setupStartCampaign() {
        startCampaignButton.setOnClickListener {
            if (validateInputs()) {
                createCampaign()
            }
        }
    }

    private fun handleAttachment(uri: Uri) {
        contentResolver.getType(uri)?.let { mimeType ->
            val attachment = Attachment(uri, mimeType)
            attachments.add(attachment)
            attachmentAdapter.notifyItemInserted(attachments.size - 1)
        }
    }

    private fun showDatePicker() {
        val calendar = scheduledDateTime ?: Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                scheduledDateTime = calendar
                updateScheduleButtons()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = scheduledDateTime ?: Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                scheduledDateTime = calendar
                updateScheduleButtons()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateScheduleButtons() {
        scheduledDateTime?.let { calendar ->
            datePickerButton.text = String.format(
                "%02d/%02d/%d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
            timePickerButton.text = String.format(
                "%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        }
    }

    private fun validateInputs(): Boolean {
        when {
            campaignNameInput.text.isNullOrBlank() -> {
                campaignNameInput.error = "Campaign name is required"
                return false
            }
            messageTemplateInput.text.isNullOrBlank() -> {
                messageTemplateInput.error = "Message template is required"
                return false
            }
            scheduleSwitch.isChecked && scheduledDateTime == null -> {
                Toast.makeText(this, "Please set schedule date and time", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun createCampaign() {
        // TODO: Create campaign and start/schedule it
        val messageTemplate = messageTemplateInput.text.toString()
        val sampleMessage = SpintaxParser.parse(messageTemplate)
        
        // For now, just show a preview
        Toast.makeText(this, "Sample message: $sampleMessage", Toast.LENGTH_LONG).show()
        
        // Return success
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
