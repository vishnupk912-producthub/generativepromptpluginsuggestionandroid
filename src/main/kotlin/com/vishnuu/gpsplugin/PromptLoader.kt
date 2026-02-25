package com.vishnuu.gpsplugin

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.intellij.openapi.diagnostic.thisLogger
import java.io.InputStream

/**
 * Data class representing a prompt entry from the JSON file
 */
data class Prompt(
    @SerializedName("intent")
    val intent: String,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("tags")
    val tags: List<String>? = null
) {
    /**
     * Get a readable title from the intent
     */
    fun getTitle(): String {
        return intent.split("_")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }
    
    /**
     * Get a readable description or fallback to prompt preview
     */
    fun getDisplayDescription(): String {
        return description ?: if (prompt.length > 100) {
            prompt.substring(0, 100) + "..."
        } else {
            prompt
        }
    }
}

/**
 * Data class for the root JSON structure
 */
data class PromptsData(
    @SerializedName("prompts")
    val prompts: List<Prompt>
)

/**
 * Utility class for loading prompts from JSON file
 */
object PromptLoader {
    private val logger = thisLogger()
    private val gson = Gson()
    
    /**
     * Load prompts from the prompts.json file in resources
     */
    fun loadPrompts(): List<Prompt> {
        return try {
            logger.info("Loading prompts from JSON file...")
            val inputStream: InputStream? = javaClass.classLoader.getResourceAsStream("prompts.json")
            if (inputStream == null) {
                logger.warn("prompts.json file not found in resources")
                logger.info("Falling back to default prompts")
                return getDefaultPrompts()
            }
            
            logger.info("Found prompts.json, parsing content...")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            logger.info("JSON content length: ${jsonString.length} characters")
            logger.info("JSON content preview: ${jsonString.take(200)}...")
            val promptsData = gson.fromJson(jsonString, PromptsData::class.java)
            logger.info("Successfully loaded ${promptsData.prompts.size} prompts from JSON")
            logger.info("First prompt: ${promptsData.prompts.firstOrNull()?.intent}")
            logger.info("Last prompt: ${promptsData.prompts.lastOrNull()?.intent}")
            promptsData.prompts
        } catch (e: Exception) {
            logger.error("Error loading prompts from JSON: ${e.message}", e)
            logger.info("Falling back to default prompts due to error")
            getDefaultPrompts()
        }
    }
    
    /**
     * Fallback prompts in case JSON loading fails
     */
    private fun getDefaultPrompts(): List<Prompt> {
        return listOf(
            Prompt(
                intent = "ui_component_design",
                prompt = "Design a Jetpack Compose UI component for the given feature, following Material 3 guidelines. Include proper theming, accessibility, and responsive design considerations.",
                category = "UI Design",
                description = "Create Material 3 compliant UI components",
                tags = listOf("compose", "material3", "ui")
            ),
            Prompt(
                intent = "navigation_setup",
                prompt = "Set up navigation in a Jetpack Compose app using Navigation Compose. Include proper type safety, deep linking, and back stack management.",
                category = "Navigation",
                description = "Implement navigation with Navigation Compose",
                tags = listOf("navigation", "compose", "routing")
            ),
            Prompt(
                intent = "state_management",
                prompt = "Implement state management for a Jetpack Compose screen using ViewModel, StateFlow, and proper state hoisting patterns.",
                category = "State Management",
                description = "Manage state with ViewModel and StateFlow",
                tags = listOf("state", "viewmodel", "stateflow")
            )
        )
    }
    
    /**
     * Filter prompts based on search query
     */
    fun filterPrompts(prompts: List<Prompt>, query: String): List<Prompt> {
        if (query.isBlank()) return prompts
        
        val lowercaseQuery = query.lowercase()
        return prompts.filter { prompt ->
            prompt.intent.lowercase().contains(lowercaseQuery) ||
            prompt.prompt.lowercase().contains(lowercaseQuery) ||
            prompt.category?.lowercase()?.contains(lowercaseQuery) == true ||
            prompt.description?.lowercase()?.contains(lowercaseQuery) == true ||
            prompt.tags?.any { it.lowercase().contains(lowercaseQuery) } == true
        }
    }
}
