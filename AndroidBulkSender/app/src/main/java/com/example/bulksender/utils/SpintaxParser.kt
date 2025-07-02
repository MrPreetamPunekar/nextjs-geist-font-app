package com.example.bulksender.utils

import kotlin.random.Random

object SpintaxParser {
    private val spintaxPattern = """\{([^{}]*)\}""".toRegex()
    private val variablePattern = """\{([A-Za-z]+)\}""".toRegex()

    /**
     * Parses a message template containing spintax and variables
     * @param template The message template (e.g., "{Hi|Hello|Hey} {Name}!")
     * @param variables Map of variable names to their values (e.g., "Name" to "John")
     * @return Processed message with random spintax selections and replaced variables
     */
    fun parse(template: String, variables: Map<String, String> = emptyMap()): String {
        var result = template

        // First, process spintax
        while (true) {
            val match = spintaxPattern.find(result) ?: break
            val options = match.groupValues[1].split("|")
            val selected = options[Random.nextInt(options.size)].trim()
            result = result.replaceRange(match.range, selected)
        }

        // Then, replace variables
        variables.forEach { (key, value) ->
            result = result.replace("{$key}", value)
        }

        return result
    }

    /**
     * Extracts all variable names from a template
     * @param template The message template
     * @return List of variable names found in the template
     */
    fun extractVariables(template: String): List<String> {
        return variablePattern.findAll(template)
            .map { it.groupValues[1] }
            .toList()
    }

    /**
     * Validates if a template has valid spintax syntax
     * @param template The message template to validate
     * @return true if the template has valid syntax, false otherwise
     */
    fun isValidTemplate(template: String): Boolean {
        var openBraces = 0
        
        template.forEach { char ->
            when (char) {
                '{' -> openBraces++
                '}' -> {
                    openBraces--
                    if (openBraces < 0) return false
                }
            }
        }
        
        return openBraces == 0
    }

    /**
     * Previews all possible variations of a spintax template
     * @param template The message template
     * @return List of all possible message variations
     */
    fun previewVariations(template: String): List<String> {
        if (!template.contains("{") || !template.contains("}")) {
            return listOf(template)
        }

        val match = spintaxPattern.find(template) ?: return listOf(template)
        val options = match.groupValues[1].split("|")
        val results = mutableListOf<String>()

        options.forEach { option ->
            val newTemplate = template.replaceRange(match.range, option.trim())
            results.addAll(previewVariations(newTemplate))
        }

        return results.distinct()
    }

    /**
     * Estimates the number of possible variations in a template
     * @param template The message template
     * @return Number of possible variations
     */
    fun countVariations(template: String): Int {
        return spintaxPattern.findAll(template)
            .map { match -> match.groupValues[1].split("|").size }
            .fold(1) { acc, count -> acc * count }
    }

    /**
     * Example usage:
     * val template = "{Hi|Hello|Hey} {Name}! How {are you|is it going}?"
     * val variables = mapOf("Name" to "John")
     * 
     * val message = parse(template, variables)
     * // Possible output: "Hello John! How is it going?"
     * 
     * val vars = extractVariables(template)
     * // Returns: ["Name"]
     * 
     * val variations = previewVariations(template)
     * // Returns all possible combinations
     * 
     * val count = countVariations(template)
     * // Returns: 6 (3 * 1 * 2)
     */
}
