package com.example.bulksender.utils

import kotlin.random.Random

object DelayUtil {
    /**
     * Returns a random delay in milliseconds between the specified min and max values
     * @param minMillis Minimum delay in milliseconds
     * @param maxMillis Maximum delay in milliseconds
     * @return Random delay between minMillis and maxMillis
     */
    fun getRandomDelay(minMillis: Long, maxMillis: Long): Long {
        return Random.nextLong(minMillis, maxMillis + 1)
    }

    /**
     * Returns a random typing delay based on message length
     * @param messageLength Length of the message to simulate typing for
     * @return Delay in milliseconds
     */
    fun getTypingDelay(messageLength: Int): Long {
        // Average typing speed: 40 WPM = 200 characters per minute = 3.33 characters per second
        val baseDelay = (messageLength / 3.33 * 1000).toLong()
        
        // Add some randomness (±20%)
        val variation = (baseDelay * 0.2).toLong()
        return baseDelay + Random.nextLong(-variation, variation + 1)
    }

    /**
     * Returns a human-like delay between messages
     * @return Delay in milliseconds
     */
    fun getMessageDelay(): Long {
        // Random delay between 5-10 seconds
        return getRandomDelay(5000, 10000)
    }

    /**
     * Returns a delay for simulating "online" status before sending
     * @return Delay in milliseconds
     */
    fun getOnlineDelay(): Long {
        // Random delay between 1-3 seconds
        return getRandomDelay(1000, 3000)
    }
}
