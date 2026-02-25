# GenerativePrompts Plugin - Icon Design

## 🎨 New Black & White Prompt Icon

### Design Concept
The new icon represents **prompts and text generation** with a clean, professional black and white design that's perfect for IntelliJ/Android Studio integration.

### Visual Elements

#### **Main Components**
1. **Circular Background**: White circle with black border for clean, modern look
2. **Document Symbol**: Rounded rectangle representing a text document/prompt
3. **Text Lines**: 5 horizontal lines of varying lengths representing prompt content
4. **Sparkle Indicator**: Small star shape suggesting creativity/ideas

#### **Design Details**
- **Size**: 16x16 pixels (standard IntelliJ plugin icon size)
- **Color Scheme**: Pure black (#000000) and white (#FFFFFF)
- **Style**: Minimalist, clean, professional
- **Theme**: Represents text, prompts, and creative writing

### Icon Breakdown

```
┌─────────────────┐
│  ⭐            │  ← Sparkle (creativity indicator)
│ ┌─────────────┐ │
│ │ ─────────── │ │  ← Document with text lines
│ │ ──────────   │ │
│ │ ─────────── │ │
│ │ ──────      │ │
│ │ ──────────   │ │
│ └─────────────┘ │
│                 │
└─────────────────┘
```

### Technical Specifications

#### **SVG Structure**
- **Background**: White circle with black stroke
- **Document**: White rectangle with black border and rounded corners
- **Text Lines**: 5 black horizontal lines of varying lengths
- **Sparkle**: Black star shape in top-right corner

#### **Dimensions**
- **ViewBox**: 16x16
- **Circle**: Center (8,8), radius 7
- **Document**: 8x10 rectangle, positioned at (4,3)
- **Lines**: Various lengths from 3-5 units
- **Sparkle**: Small 4-point star

### Why This Design Works

#### **✅ Professional**
- Clean black and white design
- Consistent with IntelliJ icon standards
- Scalable vector format

#### **✅ Meaningful**
- Document represents prompts/text
- Lines suggest written content
- Sparkle indicates creativity/ideas

#### **✅ Recognizable**
- Clear at 16x16 size
- Distinctive from other plugin icons
- Easy to identify in tool window

### Usage Context

The icon will appear in:
- **Tool Window Tab**: Right-side panel in Android Studio
- **View Menu**: When accessing the plugin
- **Plugin Manager**: When installing/updating
- **IDE Interface**: Various plugin-related dialogs

### Design Evolution

#### **Previous Icon**
- Green background with location pin
- Red X overlay (confusing)
- Not clearly related to prompts

#### **New Icon**
- Black and white professional design
- Clear document/text representation
- Sparkle suggests creativity and ideas
- Perfect for prompt/text generation context

### File Location
```
src/main/resources/icons/gps-icon.svg
```

The icon is now included in the plugin build and will be visible when the GenerativePrompts tool window is opened in Android Studio!



