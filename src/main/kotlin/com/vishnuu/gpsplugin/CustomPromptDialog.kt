package com.vishnuu.gpsplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

/**
 * Dialog for adding/editing custom prompts
 */
class CustomPromptDialog(
    private val project: Project,
    private val existingPrompt: Prompt? = null
) : DialogWrapper(project) {
    
    private val intentField = JBTextField()
    private val promptField = JBTextArea()
    private val categoryField = JBTextField()
    private val descriptionField = JBTextField()
    private val tagsField = JBTextField()
    
    init {
        title = if (existingPrompt != null) "Edit Custom Prompt" else "Add Custom Prompt"
        super.init()
        
        // Pre-fill fields if editing
        existingPrompt?.let { prompt ->
            intentField.text = prompt.intent
            promptField.text = prompt.prompt
            categoryField.text = prompt.category ?: ""
            descriptionField.text = prompt.description ?: ""
            tagsField.text = prompt.tags?.joinToString(", ") ?: ""
        }
    }
    
    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        panel.background = Color(70, 70, 70)
        panel.border = JBUI.Borders.empty(16)
        
        val constraints = GridBagConstraints()
        constraints.anchor = GridBagConstraints.WEST
        constraints.insets = JBUI.insets(8)
        
        // Intent field
        constraints.gridx = 0
        constraints.gridy = 0
        val intentLabel = createLabel("Intent (unique identifier):")
        panel.add(intentLabel, constraints)
        
        constraints.gridx = 1
        constraints.gridwidth = 2
        constraints.fill = GridBagConstraints.HORIZONTAL
        intentField.preferredSize = Dimension(300, 30)
        intentField.background = Color(80, 80, 80)
        intentField.foreground = Color(200, 200, 200)
        intentField.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(Color(100, 100, 100), 1),
            JBUI.Borders.empty(4, 8)
        )
        panel.add(intentField, constraints)
        
        // Prompt field
        constraints.gridx = 0
        constraints.gridy = 1
        constraints.gridwidth = 1
        constraints.fill = GridBagConstraints.NONE
        val promptLabel = createLabel("Prompt Text:")
        panel.add(promptLabel, constraints)
        
        constraints.gridx = 1
        constraints.gridwidth = 2
        constraints.fill = GridBagConstraints.BOTH
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        promptField.preferredSize = Dimension(300, 100)
        promptField.background = Color(80, 80, 80)
        promptField.foreground = Color(200, 200, 200)
        promptField.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(Color(100, 100, 100), 1),
            JBUI.Borders.empty(4, 8)
        )
        promptField.lineWrap = true
        promptField.wrapStyleWord = true
        panel.add(JScrollPane(promptField), constraints)
        
        // Category field
        constraints.gridx = 0
        constraints.gridy = 2
        constraints.gridwidth = 1
        constraints.fill = GridBagConstraints.NONE
        constraints.weighty = 0.0
        val categoryLabel = createLabel("Category:")
        panel.add(categoryLabel, constraints)
        
        constraints.gridx = 1
        constraints.gridwidth = 2
        constraints.fill = GridBagConstraints.HORIZONTAL
        categoryField.preferredSize = Dimension(300, 30)
        categoryField.background = Color(80, 80, 80)
        categoryField.foreground = Color(200, 200, 200)
        categoryField.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(Color(100, 100, 100), 1),
            JBUI.Borders.empty(4, 8)
        )
        panel.add(categoryField, constraints)
        
        // Description field
        constraints.gridx = 0
        constraints.gridy = 3
        constraints.gridwidth = 1
        val descriptionLabel = createLabel("Description:")
        panel.add(descriptionLabel, constraints)
        
        constraints.gridx = 1
        constraints.gridwidth = 2
        descriptionField.preferredSize = Dimension(300, 30)
        descriptionField.background = Color(80, 80, 80)
        descriptionField.foreground = Color(200, 200, 200)
        descriptionField.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(Color(100, 100, 100), 1),
            JBUI.Borders.empty(4, 8)
        )
        panel.add(descriptionField, constraints)
        
        // Tags field
        constraints.gridx = 0
        constraints.gridy = 4
        constraints.gridwidth = 1
        val tagsLabel = createLabel("Tags (comma-separated):")
        panel.add(tagsLabel, constraints)
        
        constraints.gridx = 1
        constraints.gridwidth = 2
        tagsField.preferredSize = Dimension(300, 30)
        tagsField.background = Color(80, 80, 80)
        tagsField.foreground = Color(200, 200, 200)
        tagsField.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(Color(100, 100, 100), 1),
            JBUI.Borders.empty(4, 8)
        )
        panel.add(tagsField, constraints)
        
        return panel
    }
    
    private fun createLabel(text: String): JBLabel {
        val label = JBLabel(text)
        label.foreground = Color(200, 200, 200)
        label.font = label.font.deriveFont(Font.BOLD, 12f)
        return label
    }
    
    override fun doOKAction() {
        if (validateInput()) {
            super.doOKAction()
        }
    }
    
    private fun validateInput(): Boolean {
        if (intentField.text.trim().isEmpty()) {
            showError("Intent is required")
            return false
        }
        
        if (promptField.text.trim().isEmpty()) {
            showError("Prompt text is required")
            return false
        }
        
        // Check if intent already exists (if not editing the same prompt)
        val customPromptsService = project.getService(CustomPromptsService::class.java)
        if (existingPrompt?.intent != intentField.text.trim() && 
            customPromptsService.isCustomPrompt(intentField.text.trim())) {
            showError("A prompt with this intent already exists")
            return false
        }
        
        return true
    }
    
    private fun showError(message: String) {
        JOptionPane.showMessageDialog(
            contentPanel,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE
        )
    }
    
    fun getPrompt(): Prompt {
        val tags = tagsField.text.trim()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        
        return Prompt(
            intent = intentField.text.trim(),
            prompt = promptField.text.trim(),
            category = categoryField.text.trim().takeIf { it.isNotEmpty() },
            description = descriptionField.text.trim().takeIf { it.isNotEmpty() },
            tags = tags.takeIf { it.isNotEmpty() }
        )
    }
}
