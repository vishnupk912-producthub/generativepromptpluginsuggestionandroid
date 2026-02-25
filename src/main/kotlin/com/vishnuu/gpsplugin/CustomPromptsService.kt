package com.vishnuu.gpsplugin

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter
import java.io.FileReader

/**
 * Service for managing custom user prompts
 */
@Service(Service.Level.PROJECT)
class CustomPromptsService(private val project: Project) {
    private val gson = Gson()
    private val customPromptsFile: File = File(project.basePath, ".idea/custom-prompts.json")
    
    fun getCustomPrompts(): List<Prompt> {
        return try {
            println("DEBUG: Getting custom prompts from: ${customPromptsFile.absolutePath}")
            if (!customPromptsFile.exists()) {
                println("DEBUG: Custom prompts file does not exist")
                return emptyList()
            }
            
            val jsonString = FileReader(customPromptsFile).use { it.readText() }
            val type = object : TypeToken<List<Prompt>>() {}.type
            val prompts = gson.fromJson<List<Prompt>>(jsonString, type) ?: emptyList()
            println("DEBUG: Loaded ${prompts.size} custom prompts from file")
            prompts
        } catch (e: Exception) {
            println("DEBUG: Error loading custom prompts: ${e.message}")
            emptyList()
        }
    }
    
    fun addCustomPrompt(prompt: Prompt) {
        println("DEBUG: Adding custom prompt to service: ${prompt.intent}")
        val existingPrompts = getCustomPrompts().toMutableList()
        existingPrompts.add(prompt)
        println("DEBUG: Total prompts after adding: ${existingPrompts.size}")
        saveCustomPrompts(existingPrompts)
        println("DEBUG: Custom prompt saved to file: ${customPromptsFile.absolutePath}")
    }
    
    fun updateCustomPrompt(oldIntent: String, newPrompt: Prompt) {
        val existingPrompts = getCustomPrompts().toMutableList()
        val index = existingPrompts.indexOfFirst { it.intent == oldIntent }
        if (index != -1) {
            existingPrompts[index] = newPrompt
            saveCustomPrompts(existingPrompts)
        }
    }
    
    fun deleteCustomPrompt(intent: String) {
        val existingPrompts = getCustomPrompts().toMutableList()
        existingPrompts.removeAll { it.intent == intent }
        saveCustomPrompts(existingPrompts)
    }
    
    private fun saveCustomPrompts(prompts: List<Prompt>) {
        try {
            customPromptsFile.parentFile?.mkdirs()
            val jsonString = gson.toJson(prompts)
            FileWriter(customPromptsFile).use { it.write(jsonString) }
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }
    
    fun isCustomPrompt(intent: String): Boolean {
        return getCustomPrompts().any { it.intent == intent }
    }
}
