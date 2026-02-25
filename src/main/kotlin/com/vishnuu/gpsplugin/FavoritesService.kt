package com.vishnuu.gpsplugin

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.util.*

/**
 * Service for managing favorite prompts
 */
@Service
class FavoritesService {
    private val favorites = mutableSetOf<String>()
    
    fun getFavoriteIntents(): Set<String> = favorites.toSet()
    
    fun setFavoriteIntents(intents: List<String>) {
        favorites.clear()
        favorites.addAll(intents)
    }
    
    fun addFavorite(intent: String) {
        favorites.add(intent)
    }
    
    fun removeFavorite(intent: String) {
        favorites.remove(intent)
    }
    
    fun isFavorite(intent: String): Boolean = favorites.contains(intent)
}
