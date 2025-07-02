package com.example.bulksender.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.example.bulksender.utils.DelayUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WhatsAppAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var isProcessing = false
    private var currentMessage: String? = null
    private var currentNumber: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isProcessing || event?.packageName != "com.whatsapp") return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                // Handle different WhatsApp screens
                when {
                    isMainScreen(event) -> handleMainScreen()
                    isChatScreen(event) -> handleChatScreen()
                }
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                // Handle content changes if needed
            }
        }
    }

    override fun onInterrupt() {
        isProcessing = false
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "WhatsApp Automation Service Connected", Toast.LENGTH_SHORT).show()
    }

    fun startMessageCampaign(number: String, message: String) {
        if (isProcessing) return

        isProcessing = true
        currentNumber = number
        currentMessage = message

        // Open chat with the number
        openChat(number)
    }

    private fun isMainScreen(event: AccessibilityEvent): Boolean {
        return event.className?.toString()?.contains("Main") == true
    }

    private fun isChatScreen(event: AccessibilityEvent): Boolean {
        return event.className?.toString()?.contains("Conversation") == true
    }

    private fun handleMainScreen() {
        // Find and click the new chat button
        findNodeByDescription("New chat")?.let { newChatNode ->
            performClick(newChatNode)
            
            // Wait for search input to appear
            handler.postDelayed({
                findNodeByDescription("Search")?.let { searchNode ->
                    // Input phone number
                    val arguments = Bundle()
                    arguments.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        currentNumber
                    )
                    searchNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    
                    // Wait for contact to appear and click it
                    handler.postDelayed({
                        findNodeByText(currentNumber ?: "")?.let { contactNode ->
                            performClick(contactNode)
                        }
                    }, 1000)
                }
            }, 1000)
        }
    }

    private fun handleChatScreen() {
        currentMessage?.let { message ->
            // Find message input field
            findNodeByDescription("Type a message")?.let { messageNode ->
                // Simulate typing delay
                serviceScope.launch {
                    delay(DelayUtil.getRandomDelay(1000, 2000))
                    
                    // Input message
                    val arguments = Bundle()
                    arguments.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        message
                    )
                    messageNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    
                    // Wait and click send button
                    delay(DelayUtil.getRandomDelay(500, 1500))
                    findNodeByDescription("Send")?.let { sendNode ->
                        performClick(sendNode)
                        
                        // Message sent successfully
                        onMessageSent()
                    }
                }
            }
        }
    }

    private fun openChat(number: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setPackage("com.whatsapp")
            data = android.net.Uri.parse("https://api.whatsapp.com/send?phone=$number")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun onMessageSent() {
        // Reset current message and number
        currentMessage = null
        currentNumber = null
        isProcessing = false
        
        // Notify listeners that message was sent
        val intent = Intent("com.example.bulksender.MESSAGE_SENT")
        sendBroadcast(intent)
    }

    private fun findNodeByDescription(description: String): AccessibilityNodeInfo? {
        return rootInActiveWindow?.findAccessibilityNodeInfosByText(description)?.firstOrNull()
    }

    private fun findNodeByText(text: String): AccessibilityNodeInfo? {
        return rootInActiveWindow?.findAccessibilityNodeInfosByText(text)?.firstOrNull()
    }

    private fun performClick(node: AccessibilityNodeInfo) {
        node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    private fun simulateClick(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        dispatchGesture(gesture, null, null)
    }

    companion object {
        private const val TAG = "WhatsAppAccessibility"
    }
}
