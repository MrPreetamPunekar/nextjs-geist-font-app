package com.example.bulksender.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bulksender.R
import com.example.bulksender.models.Attachment

class AttachmentAdapter(
    private val attachments: List<Attachment>,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attachment, parent, false)
        return AttachmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.bind(attachments[position])
    }

    override fun getItemCount(): Int = attachments.size

    inner class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileTypeIcon: ImageView = itemView.findViewById(R.id.fileTypeIcon)
        private val fileNameText: TextView = itemView.findViewById(R.id.fileNameText)
        private val fileTypeText: TextView = itemView.findViewById(R.id.fileTypeText)
        private val removeButton: ImageButton = itemView.findViewById(R.id.removeButton)

        fun bind(attachment: Attachment) {
            // Set file type icon based on mime type
            fileTypeIcon.setImageResource(
                when {
                    attachment.isImage -> R.drawable.ic_image
                    attachment.isVideo -> R.drawable.ic_video
                    else -> R.drawable.ic_document
                }
            )

            // Set file name
            fileNameText.text = attachment.fileName ?: attachment.uri.lastPathSegment ?: "Unknown file"

            // Set file type description
            fileTypeText.text = when {
                attachment.isImage -> "Image"
                attachment.isVideo -> "Video"
                else -> "Document"
            }

            // Set remove button click listener
            removeButton.setOnClickListener {
                onRemoveClick(adapterPosition)
            }
        }
    }

    companion object {
        private const val TAG = "AttachmentAdapter"
    }
}
