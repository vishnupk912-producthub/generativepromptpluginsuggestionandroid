# Generative Prompts Plugin

An IntelliJ Platform plugin for Android Studio that provides a convenient way to access and use pre-defined prompts for Android development, especially for Jetpack Compose and Material 3 design patterns.

## Features

- 📋 Pre-defined prompts for Android development
- 🎨 Jetpack Compose UI component prompts  
- 🔍 Search and filter functionality
- 📱 Material 3 design guidelines
- 📋 One-click copy to clipboard (click to select, double-click to copy)
- 🏷️ Categorized prompts with tags

## Project Structure

```
Generative Prompts/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradle/wrapper/
├── src/main/kotlin/com/vishnuu/gpsplugin/
│   ├── GpsToolWindowFactory.kt
│   ├── PromptLoader.kt
│   └── ShowGpsToolWindowAction.kt
├── src/main/resources/
│   ├── META-INF/plugin.xml
│   ├── icons/gps-icon.svg
│   └── prompts.json
└── README.md
```

## Building the Plugin

### Prerequisites

- Java 17 or higher
- IntelliJ IDEA or Android Studio
- Gradle (included via wrapper)

### Build Commands

1. **Build the plugin:**
   ```bash
   ./gradlew buildPlugin
   ```

2. **Run the plugin in development mode:**
   ```bash
   ./gradlew runIde
   ```

3. **Clean build:**
   ```bash
   ./gradlew clean buildPlugin
   ```

The built plugin will be available in `build/distributions/` as a `.zip` file.

## Installation in Android Studio

### Method 1: Install from Disk

1. Build the plugin using `./gradlew buildPlugin`
2. Open Android Studio
3. Go to **File** → **Settings** (or **Android Studio** → **Preferences** on macOS)
4. Navigate to **Plugins**
5. Click the gear icon ⚙️ and select **Install Plugin from Disk...**
6. Select the `.zip` file from `build/distributions/`
7. Restart Android Studio

### Method 2: Install from Marketplace (Future)

Once published to the JetBrains Marketplace, you can install it directly from the plugins marketplace.

## Usage

1. **Open the Tool Window:**
   - Go to **View** → **Tool Windows** → **Generative Prompts**
   - Or use the action in the View menu

2. **Browse Prompts:**
   - The tool window will show all available prompts
   - Each prompt displays title, category, description, and tags

3. **Search and Filter:**
   - Use the search field to filter prompts by intent, category, or content
   - Search is case-insensitive and matches multiple fields

4. **Select and Copy Prompts:**
   - **Single Click**: Select a prompt from the list
   - **Double Click**: Instantly copy the prompt to clipboard
   - **Copy Button**: Click the "Copy Prompt" button to copy selected prompt
   - The prompt text will be copied to your clipboard

## Customizing Prompts

You can customize the prompts by editing the `src/main/resources/prompts.json` file:

```json
{
  "prompts": [
    {
      "intent": "your_custom_intent",
      "prompt": "Your custom prompt text here...",
      "category": "Your Category",
      "description": "Brief description of the prompt",
      "tags": ["tag1", "tag2", "tag3"]
    }
  ]
}
```

### Prompt Fields

- **intent**: Unique identifier (used for title generation)
- **prompt**: The actual prompt text to copy
- **category**: Optional category for grouping
- **description**: Optional description shown in the UI
- **tags**: Optional array of tags for filtering

## Development

### Project Configuration

- **Kotlin Version**: 1.9.21
- **IntelliJ Plugin Version**: 1.17.0
- **Target IDE**: Android Studio (IntelliJ 2023.3+)
- **Java Version**: 17

### Key Components

- **GpsToolWindowFactory**: Creates the tool window content
- **PromptLoader**: Loads and parses prompts from JSON
- **ShowGpsToolWindowAction**: Action to show the tool window
- **GpsToolWindowContent**: Main UI implementation

### Adding New Features

1. **New UI Components**: Add to `GpsToolWindowContent`
2. **New Prompt Types**: Extend the `Prompt` data class
3. **New Actions**: Create action classes and register in `plugin.xml`

## Troubleshooting

### Build Issues

- Ensure Java 17+ is installed and set as JAVA_HOME
- Check that all dependencies are properly resolved
- Verify IntelliJ plugin version compatibility

### Runtime Issues

- Check that the plugin is enabled in Android Studio
- Verify the tool window is visible in View → Tool Windows
- Check the IDE logs for any error messages

### Plugin Not Loading

- Ensure the plugin is compatible with your Android Studio version
- Check that all required dependencies are available
- Verify the plugin.xml configuration

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Support

For issues and questions:
- Create an issue in the repository
- Check the troubleshooting section above
- Review the IntelliJ Platform documentation
