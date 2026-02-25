package com.vishnuu.gpsplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.*

/**
 * Factory for creating the GPS Prompts tool window
 */
class GpsToolWindowFactory : ToolWindowFactory {
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = GpsToolWindowContent(project)
        toolWindow.contentManager.addContent(
            toolWindow.contentManager.factory.createContent(content, null, false)
        )
    }
}

/**
 * Main content panel for the GPS Prompts tool window
 */
class GpsToolWindowContent(private val project: Project) : JBPanel<GpsToolWindowContent>(BorderLayout()) {
    
    private val promptListModel = DefaultListModel<Prompt>()
    private val promptList = JBList(promptListModel)
    private val searchField = JBTextField()
    private val copyButton = JButton("Copy Prompt")
    private val favoriteButton = JButton("⭐ Add to Favorites")
    private val addCustomButton = JButton("➕ Add Custom")
    private val editCustomButton = JButton("✏️ Edit")
    private val deleteCustomButton = JButton("🗑️ Delete")
    private val statusLabel = JBLabel("Click a prompt to select, double-click to copy")
    
    // Favorites functionality
    private val favoritePrompts = mutableSetOf<String>()
    private val favoriteListModel = DefaultListModel<Prompt>()
    private val favoriteList = JBList(favoriteListModel)
    private val customPromptsListModel = DefaultListModel<Prompt>()
    private val customPromptsList = JBList(customPromptsListModel)
    private val tabbedPane = JTabbedPane()
    
    private var allPrompts: List<Prompt> = emptyList()
    private var customPrompts: List<Prompt> = emptyList()
    
    init {
        setupUI()
        loadPrompts()
        loadCustomPrompts()
        loadFavorites()
        setupEventHandlers()
    }
    
    private fun setupUI() {
        // Main panel setup with dark theme
        preferredSize = Dimension(400, 600)
        minimumSize = Dimension(300, 400)
        border = JBUI.Borders.empty(8)
        background = Color(60, 60, 60) // Grey background
        foreground = Color(200, 200, 200) // Light grey text
        
        // Ensure the panel can handle scrolling properly
        layout = BorderLayout()
        
        // Search panel with grey theme
        val searchPanel = JBPanel<JBPanel<*>>(FlowLayout(FlowLayout.LEFT))
        searchPanel.background = Color(70, 70, 70) // Grey panel background
        searchPanel.foreground = Color(200, 200, 200)
        
        val searchLabel = JBLabel("Search:")
        searchLabel.foreground = Color(200, 200, 200)
        searchPanel.add(searchLabel)
        
        searchField.preferredSize = Dimension(200, 30)
        searchField.toolTipText = "Search prompts by intent, category, or content"
        searchField.background = Color(80, 80, 80) // Grey input background
        searchField.foreground = Color(200, 200, 200) // Light grey text
        searchField.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(Color(100, 100, 100), 1),
            JBUI.Borders.empty(4, 8)
        )
        searchPanel.add(searchField)
        add(searchPanel, BorderLayout.NORTH)
        
        // Setup tabbed pane for All Prompts and Favorites
        setupTabbedPane()
        
        // Button panel with both copy and favorite buttons
        val buttonPanel = JBPanel<JBPanel<*>>(FlowLayout(FlowLayout.CENTER))
        buttonPanel.background = Color(70, 70, 70)
        
        copyButton.isEnabled = false
        copyButton.preferredSize = Dimension(120, 35)
        copyButton.background = Color(100, 100, 100) // Grey button
        copyButton.foreground = Color.WHITE
        copyButton.border = JBUI.Borders.customLine(Color(120, 120, 120), 1)
        copyButton.font = copyButton.font.deriveFont(Font.BOLD, 12f)
        
        favoriteButton.isEnabled = false
        favoriteButton.preferredSize = Dimension(180, 35)
        favoriteButton.background = Color(120, 120, 120) // Grey color
        favoriteButton.foreground = Color.WHITE
        favoriteButton.font = favoriteButton.font.deriveFont(Font.BOLD, 12f)
        
        // Custom prompt buttons
        addCustomButton.preferredSize = Dimension(120, 35)
        addCustomButton.background = Color(140, 140, 140) // Grey color
        addCustomButton.foreground = Color.WHITE
        addCustomButton.font = addCustomButton.font.deriveFont(Font.BOLD, 12f)
        
        editCustomButton.isEnabled = false
        editCustomButton.preferredSize = Dimension(80, 35)
        editCustomButton.background = Color(160, 160, 160) // Grey color
        editCustomButton.foreground = Color.WHITE
        editCustomButton.font = editCustomButton.font.deriveFont(Font.BOLD, 12f)
        
        deleteCustomButton.isEnabled = false
        deleteCustomButton.preferredSize = Dimension(80, 35)
        deleteCustomButton.background = Color(180, 180, 180) // Grey color
        deleteCustomButton.foreground = Color.WHITE
        deleteCustomButton.font = deleteCustomButton.font.deriveFont(Font.BOLD, 12f)
        
        buttonPanel.add(copyButton)
        buttonPanel.add(favoriteButton)
        buttonPanel.add(addCustomButton)
        buttonPanel.add(editCustomButton)
        buttonPanel.add(deleteCustomButton)
        
        // Status panel with grey theme
        val statusPanel = JBPanel<JBPanel<*>>(FlowLayout(FlowLayout.LEFT))
        statusPanel.background = Color(70, 70, 70)
        statusLabel.foreground = Color(200, 200, 200)
        statusLabel.font = statusLabel.font.deriveFont(11f)
        statusPanel.add(statusLabel)
        
        // Combine button and status panels
        val bottomPanel = JBPanel<JBPanel<*>>(BorderLayout())
        bottomPanel.background = Color(70, 70, 70)
        bottomPanel.add(buttonPanel, BorderLayout.CENTER)
        bottomPanel.add(statusPanel, BorderLayout.SOUTH)
        add(bottomPanel, BorderLayout.SOUTH)
    }
    
    private fun setupTabbedPane() {
        // Configure tabbed pane with grey theme
        tabbedPane.background = Color(70, 70, 70)
        tabbedPane.foreground = Color(200, 200, 200)
        
        // All Prompts Tab
        val allPromptsPanel = createPromptsPanel(promptList, "All Prompts")
        tabbedPane.addTab("All Prompts", allPromptsPanel)
        
        // Favorites Tab
        val favoritesPanel = createPromptsPanel(favoriteList, "Favorites")
        tabbedPane.addTab("⭐ Favorites", favoritesPanel)
        
        // Custom Prompts Tab
        val customPromptsPanel = createPromptsPanel(customPromptsList, "Custom Prompts")
        tabbedPane.addTab("🔧 Custom", customPromptsPanel)
        
        add(tabbedPane, BorderLayout.CENTER)
    }
    
    private fun createPromptsPanel(list: JBList<Prompt>, title: String): JBPanel<*> {
        val panel = JBPanel<JBPanel<*>>(BorderLayout())
        panel.background = Color(80, 80, 80)
        
        // Configure the list with grey theme
        list.cellRenderer = PromptListCellRenderer()
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        list.background = Color(80, 80, 80) // Grey list background
        list.foreground = Color(200, 200, 200) // Light grey text
        list.selectionBackground = Color(120, 120, 120) // Grey selection
        list.selectionForeground = Color.WHITE
        
        // Add keyboard navigation support
        list.addKeyListener(object : java.awt.event.KeyAdapter() {
            override fun keyPressed(e: java.awt.event.KeyEvent) {
                when (e.keyCode) {
                    java.awt.event.KeyEvent.VK_ENTER -> {
                        val selectedPrompt = list.selectedValue as? Prompt
                        if (selectedPrompt != null) {
                            copyToClipboard(selectedPrompt.prompt)
                            statusLabel.text = "Copied: ${selectedPrompt.getTitle()}"
                        }
                    }
                    java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.VK_DOWN -> {
                        // Ensure selected item is visible when navigating with keyboard
                        val selectedIndex = list.selectedIndex
                        if (selectedIndex >= 0) {
                            list.ensureIndexIsVisible(selectedIndex)
                        }
                    }
                }
            }
        })
        
        // Add mouse listener for click-to-select functionality
        list.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 1) { // Single click
                    val index = list.locationToIndex(e.point)
                    if (index >= 0) {
                        list.selectedIndex = index
                        val selectedPrompt = list.selectedValue as? Prompt
                        if (selectedPrompt != null) {
                            copyButton.isEnabled = true
                            favoriteButton.isEnabled = true
                            updateFavoriteButton(selectedPrompt)
                            statusLabel.text = "Selected: ${selectedPrompt.getTitle()}"
                        }
                    }
                } else if (e.clickCount == 2) { // Double click to copy
                    val index = list.locationToIndex(e.point)
                    if (index >= 0) {
                        list.selectedIndex = index
                        val selectedPrompt = list.selectedValue as? Prompt
                        if (selectedPrompt != null) {
                            copyToClipboard(selectedPrompt.prompt)
                            statusLabel.text = "Copied: ${selectedPrompt.getTitle()}"
                        }
                    }
                }
            }
        })
        
        // Add mouse wheel support for scrolling
        list.addMouseWheelListener { e ->
            val scrollPane = list.parent as? JScrollPane
            if (scrollPane != null) {
                val scrollBar = scrollPane.verticalScrollBar
                val unitIncrement = scrollBar.unitIncrement
                val scrollAmount = e.wheelRotation * unitIncrement
                scrollBar.value += scrollAmount
            }
        }
        
        // Create scroll pane with enhanced scrolling and dark theme
        val scrollPane = JBScrollPane(list)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS // Always show scrollbar
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        
        // Grey theme for scrollbar with enhanced draggable styling
        scrollPane.background = Color(80, 80, 80)
        
        // Configure the vertical scrollbar for better dragging
        val verticalScrollBar = scrollPane.verticalScrollBar
        verticalScrollBar.background = Color(100, 100, 100)
        verticalScrollBar.foreground = Color(140, 140, 140)
        verticalScrollBar.preferredSize = Dimension(24, 0) // Even wider for easier dragging
        verticalScrollBar.minimumSize = Dimension(24, 0)
        
        // Enhanced scrolling with custom slider controls
        verticalScrollBar.unitIncrement = 16 // Smoother scrolling
        verticalScrollBar.blockIncrement = 64
        
        // Make scrollbar more visible and draggable
        verticalScrollBar.isOpaque = true
        verticalScrollBar.isVisible = true
        verticalScrollBar.isEnabled = true
        verticalScrollBar.border = JBUI.Borders.customLine(Color(120, 120, 120), 1)
        
        // Make scrollbar more responsive to dragging
        verticalScrollBar.addAdjustmentListener { e ->
            if (!e.valueIsAdjusting) {
                // Ensure smooth scrolling when dragging
                list.ensureIndexIsVisible(list.selectedIndex)
            }
        }
        
        // Set preferred size for the scroll pane
        scrollPane.preferredSize = Dimension(400, 500)
        scrollPane.minimumSize = Dimension(300, 200)
        
        // Ensure the scroll pane is focusable and can receive keyboard events
        scrollPane.isFocusable = true
        scrollPane.requestFocusInWindow()
        
        panel.add(scrollPane, BorderLayout.CENTER)
        return panel
    }
    
    private fun loadPrompts() {
        allPrompts = PromptLoader.loadPrompts()
        updatePromptList(allPrompts)
        statusLabel.text = "Loaded ${allPrompts.size} prompts - Click to select, double-click to copy"
    }
    
    private fun loadCustomPrompts() {
        try {
            val customPromptsService = project.getService(CustomPromptsService::class.java)
            customPrompts = customPromptsService.getCustomPrompts()
            println("DEBUG: Loaded ${customPrompts.size} custom prompts")
            updateCustomPromptsList()
        } catch (e: Exception) {
            println("DEBUG: Error loading custom prompts: ${e.message}")
            customPrompts = emptyList()
            updateCustomPromptsList()
        }
    }
    
    private fun loadFavorites() {
        try {
            val settings = project.getService(FavoritesService::class.java)
            favoritePrompts.clear()
            favoritePrompts.addAll(settings.getFavoriteIntents())
            updateFavoriteList()
        } catch (e: Exception) {
            // Service not available, use in-memory storage
            favoritePrompts.clear()
        }
    }
    
    private fun saveFavorites() {
        try {
            val settings = project.getService(FavoritesService::class.java)
            settings.setFavoriteIntents(favoritePrompts.toList())
        } catch (e: Exception) {
            // Service not available, keep in-memory storage
        }
    }
    
    private fun updatePromptList(prompts: List<Prompt>) {
        promptListModel.clear()
        // Combine built-in prompts with custom prompts
        val allPromptsCombined = (allPrompts + customPrompts).distinctBy { it.intent }
        val filteredPrompts = PromptLoader.filterPrompts(allPromptsCombined, searchField.text.trim())
        filteredPrompts.forEach { promptListModel.addElement(it) }
        
        // Scroll to top when updating the list
        if (filteredPrompts.isNotEmpty()) {
            promptList.ensureIndexIsVisible(0)
        }
    }
    
    private fun refreshPromptList() {
        updatePromptList(allPrompts)
    }
    
    private fun updateFavoriteList() {
        favoriteListModel.clear()
        val favorites = allPrompts.filter { favoritePrompts.contains(it.intent) }
        favorites.forEach { favoriteListModel.addElement(it) }
    }
    
    private fun updateCustomPromptsList() {
        customPromptsListModel.clear()
        customPrompts.forEach { customPromptsListModel.addElement(it) }
        println("DEBUG: Updated custom prompts list with ${customPrompts.size} items")
    }
    
    private fun setupEventHandlers() {
        // Search functionality
        searchField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) = performSearch()
            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) = performSearch()
            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) = performSearch()
        })
        
        // List selection for both lists
        promptList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                val selectedPrompt = promptList.selectedValue as? Prompt
                copyButton.isEnabled = selectedPrompt != null
                favoriteButton.isEnabled = selectedPrompt != null
                editCustomButton.isEnabled = selectedPrompt != null && isCustomPrompt(selectedPrompt)
                deleteCustomButton.isEnabled = selectedPrompt != null && isCustomPrompt(selectedPrompt)
                if (selectedPrompt != null) {
                    updateFavoriteButton(selectedPrompt)
                }
                statusLabel.text = if (selectedPrompt != null) {
                    "Selected: ${selectedPrompt.getTitle()}"
                } else {
                    "Click a prompt to select, double-click to copy"
                }
            }
        }
        
        favoriteList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                val selectedPrompt = favoriteList.selectedValue as? Prompt
                copyButton.isEnabled = selectedPrompt != null
                favoriteButton.isEnabled = selectedPrompt != null
                editCustomButton.isEnabled = selectedPrompt != null && isCustomPrompt(selectedPrompt)
                deleteCustomButton.isEnabled = selectedPrompt != null && isCustomPrompt(selectedPrompt)
                if (selectedPrompt != null) {
                    updateFavoriteButton(selectedPrompt)
                }
                statusLabel.text = if (selectedPrompt != null) {
                    "Selected: ${selectedPrompt.getTitle()}"
                } else {
                    "Click a prompt to select, double-click to copy"
                }
            }
        }
        
        customPromptsList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                val selectedPrompt = customPromptsList.selectedValue as? Prompt
                copyButton.isEnabled = selectedPrompt != null
                favoriteButton.isEnabled = selectedPrompt != null
                editCustomButton.isEnabled = selectedPrompt != null && isCustomPrompt(selectedPrompt)
                deleteCustomButton.isEnabled = selectedPrompt != null && isCustomPrompt(selectedPrompt)
                if (selectedPrompt != null) {
                    updateFavoriteButton(selectedPrompt)
                }
                statusLabel.text = if (selectedPrompt != null) {
                    "Selected: ${selectedPrompt.getTitle()}"
                } else {
                    "Click a prompt to select, double-click to copy"
                }
            }
        }
        
        // Copy button
        copyButton.addActionListener {
            val selectedPrompt = getCurrentSelectedPrompt()
            if (selectedPrompt != null) {
                copyToClipboard(selectedPrompt.prompt)
                statusLabel.text = "Copied: ${selectedPrompt.getTitle()}"
            }
        }
        
        // Favorite button
        favoriteButton.addActionListener {
            val selectedPrompt = getCurrentSelectedPrompt()
            if (selectedPrompt != null) {
                toggleFavorite(selectedPrompt)
            }
        }
        
        // Add custom prompt button
        addCustomButton.addActionListener {
            showAddCustomPromptDialog()
        }
        
        // Edit custom prompt button
        editCustomButton.addActionListener {
            val selectedPrompt = getCurrentSelectedPrompt()
            if (selectedPrompt != null && isCustomPrompt(selectedPrompt)) {
                showEditCustomPromptDialog(selectedPrompt)
            }
        }
        
        // Delete custom prompt button
        deleteCustomButton.addActionListener {
            val selectedPrompt = getCurrentSelectedPrompt()
            if (selectedPrompt != null && isCustomPrompt(selectedPrompt)) {
                showDeleteCustomPromptDialog(selectedPrompt)
            }
        }
    }
    
    private fun getCurrentSelectedPrompt(): Prompt? {
        return when (tabbedPane.selectedIndex) {
            0 -> promptList.selectedValue as? Prompt
            1 -> favoriteList.selectedValue as? Prompt
            2 -> customPromptsList.selectedValue as? Prompt
            else -> null
        }
    }
    
    private fun updateFavoriteButton(prompt: Prompt) {
        val isFavorite = favoritePrompts.contains(prompt.intent)
        favoriteButton.text = if (isFavorite) "⭐ Remove from Favorites" else "⭐ Add to Favorites"
        favoriteButton.background = if (isFavorite) Color(220, 53, 69) else Color(255, 193, 7)
    }
    
    private fun toggleFavorite(prompt: Prompt) {
        if (favoritePrompts.contains(prompt.intent)) {
            favoritePrompts.remove(prompt.intent)
            statusLabel.text = "Removed from favorites: ${prompt.getTitle()}"
        } else {
            favoritePrompts.add(prompt.intent)
            statusLabel.text = "Added to favorites: ${prompt.getTitle()}"
        }
        
        saveFavorites()
        updateFavoriteList()
        updateFavoriteButton(prompt)
    }
    
    private fun performSearch() {
        val query = searchField.text.trim()
        val allPromptsCombined = (allPrompts + customPrompts).distinctBy { it.intent }
        val filteredPrompts = PromptLoader.filterPrompts(allPromptsCombined, query)
        updatePromptList(filteredPrompts)
        statusLabel.text = "Found ${filteredPrompts.size} prompts - Click to select, double-click to copy"
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(text)
        clipboard.setContents(selection, null)
    }
    
    fun isCustomPrompt(prompt: Prompt): Boolean {
        return customPrompts.any { it.intent == prompt.intent }
    }
    
    private fun showAddCustomPromptDialog() {
        val dialog = CustomPromptDialog(project)
        if (dialog.showAndGet()) {
            val newPrompt = dialog.getPrompt()
            try {
                println("DEBUG: Adding custom prompt: ${newPrompt.intent}")
                val customPromptsService = project.getService(CustomPromptsService::class.java)
                customPromptsService.addCustomPrompt(newPrompt)
                println("DEBUG: Custom prompt added to service")
                loadCustomPrompts()
                refreshPromptList()
                updateCustomPromptsList()
                statusLabel.text = "Added custom prompt: ${newPrompt.getTitle()}"
            } catch (e: Exception) {
                println("DEBUG: Error adding custom prompt: ${e.message}")
                statusLabel.text = "Error adding custom prompt: ${e.message}"
            }
        }
    }
    
    private fun showEditCustomPromptDialog(prompt: Prompt) {
        val dialog = CustomPromptDialog(project, prompt)
        if (dialog.showAndGet()) {
            val updatedPrompt = dialog.getPrompt()
            try {
                val customPromptsService = project.getService(CustomPromptsService::class.java)
                customPromptsService.updateCustomPrompt(prompt.intent, updatedPrompt)
                loadCustomPrompts()
                refreshPromptList()
                updateCustomPromptsList()
                statusLabel.text = "Updated custom prompt: ${updatedPrompt.getTitle()}"
            } catch (e: Exception) {
                statusLabel.text = "Error updating custom prompt: ${e.message}"
            }
        }
    }
    
    private fun showDeleteCustomPromptDialog(prompt: Prompt) {
        val result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the custom prompt '${prompt.getTitle()}'?",
            "Delete Custom Prompt",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        )
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                val customPromptsService = project.getService(CustomPromptsService::class.java)
                customPromptsService.deleteCustomPrompt(prompt.intent)
                loadCustomPrompts()
                refreshPromptList()
                updateCustomPromptsList()
                statusLabel.text = "Deleted custom prompt: ${prompt.getTitle()}"
            } catch (e: Exception) {
                statusLabel.text = "Error deleting custom prompt: ${e.message}"
            }
        }
    }
}

/**
 * Custom cell renderer for the prompt list
 */
class PromptListCellRenderer : DefaultListCellRenderer() {
    
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): java.awt.Component {
        val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        
        // Set grey theme colors
        if (isSelected) {
            component.background = Color(120, 120, 120) // Grey selection
            component.foreground = Color.WHITE
        } else {
            component.background = Color(80, 80, 80) // Grey background
            component.foreground = Color(200, 200, 200) // Light grey text
        }
        
        if (value is Prompt) {
            // Check if this is a custom prompt
            val isCustom = list?.let { 
                val content = it.parent as? JBPanel<*>
                content?.let { panel ->
                    val gpsContent = panel.parent as? GpsToolWindowContent
                    gpsContent?.isCustomPrompt(value) == true
                } ?: false
            } ?: false
            
            val customIndicator = if (isCustom) "🔧 " else ""
            
            val html = buildString {
                append("<html>")
                append("<div style='padding: 6px; line-height: 1.4; font-family: Roboto, sans-serif;'>")
                append("<b style='font-family: Roboto, sans-serif; font-weight: bold;'>$customIndicator${value.getTitle()}</b>")
                if (!value.category.isNullOrBlank()) {
                    append(" <span style='color: ${if (isSelected) "#cccccc" else "#999999"}; font-size: 11px; font-family: Roboto, sans-serif;'>[${value.category}]</span>")
                }
                if (isCustom) {
                    append(" <span style='color: ${if (isSelected) "#cccccc" else "#aaaaaa"}; font-size: 10px; font-family: Roboto, sans-serif;'>[Custom]</span>")
                }
                append("<br/>")
                append("<span style='color: ${if (isSelected) "#e0e0e0" else "#bbbbbb"}; font-size: 12px; font-family: Roboto, sans-serif;'>${value.getDisplayDescription()}</span>")
                if (!value.tags.isNullOrEmpty()) {
                    append("<br/>")
                    append("<span style='color: ${if (isSelected) "#cccccc" else "#999999"}; font-size: 10px; font-family: Roboto, sans-serif;'>Tags: ${value.tags.joinToString(", ")}</span>")
                }
                append("</div>")
                append("</html>")
            }
            text = html
        }
        
        return component
    }
}