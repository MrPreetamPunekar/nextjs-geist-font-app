package com.example.bulksender.models

import android.net.Uri

data class Attachment(
    val uri: Uri,
    val mimeType: String,
    val fileName: String? = null
) {
    val isImage: Boolean
        get() = mimeType.startsWith("image/")
    
    val isVideo: Boolean
        get() = mimeType.startsWith("video/")
    
    val isDocument: Boolean
        get() = !isImage && !isVideo
}
