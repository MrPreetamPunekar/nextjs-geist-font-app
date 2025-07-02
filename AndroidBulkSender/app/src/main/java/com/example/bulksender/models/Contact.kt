package com.example.bulksender.models

data class Contact(
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val variables: Map<String, String> = emptyMap(),
    var isSelected: Boolean = true,
    var isValid: Boolean = true
) {
    companion object {
        /**
         * Validates a phone number format
         * @param number Phone number to validate
         * @return true if the number is valid, false otherwise
         */
        fun isValidPhoneNumber(number: String): Boolean {
            // Remove any whitespace, dashes, or parentheses
            val cleanNumber = number.replace(Regex("[\\s\\-()]"), "")
            
            // Check if it starts with + and contains only digits
            return cleanNumber.matches(Regex("^\\+?[0-9]{10,15}$"))
        }

        /**
         * Formats a phone number to a standard format
         * @param number Phone number to format
         * @return Formatted phone number
         */
        fun formatPhoneNumber(number: String): String {
            // Remove any non-digit characters except +
            val cleanNumber = number.replace(Regex("[^0-9+]"), "")
            
            // Ensure number starts with +
            return if (cleanNumber.startsWith("+")) {
                cleanNumber
            } else if (cleanNumber.startsWith("0")) {
                // Replace leading 0 with country code (example for US)
                "+1${cleanNumber.substring(1)}"
            } else {
                // Add default country code (example for US)
                "+1$cleanNumber"
            }
        }
    }

    /**
     * Gets all variable values for this contact
     * @param requiredVariables List of variables required by the message template
     * @return Map of variable names to their values
     */
    fun getVariableValues(requiredVariables: List<String>): Map<String, String> {
        val result = mutableMapOf<String, String>()
        
        requiredVariables.forEach { variable ->
            result[variable] = when (variable.lowercase()) {
                "name" -> name
                "phone", "number" -> phoneNumber
                else -> variables[variable] ?: ""
            }
        }
        
        return result
    }
}
