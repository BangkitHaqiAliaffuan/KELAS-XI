# 🍕 WaveOfFood - Modern Food Ordering App

A stunning, modern food ordering application built with **Jetpack Compose** featuring beautiful UI/UX design, smooth animations, and premium user experience.

## ✨ Features

### 🎨 Beautiful UI Design
- **Modern Design System** with vibrant orange color palette
- **Custom Typography** using system fonts (can be upgraded to Poppins)
- **Smooth Animations** and micro-interactions
- **Gradient Backgrounds** and elegant card designs
- **Dark Theme Support** (ready for implementation)

### 📱 Screens Implemented

1. **🚀 Splash Screen**
   - Animated logo with scale and fade effects
   - Floating particle background animation
   - Gradient background with rotation effect
   - Auto-navigation after 3 seconds

2. **👋 Onboarding Screens** (3 screens)
   - Choose Your Favorite Food
   - Fast & Safe Delivery  
   - Easy Payment Method
   - Horizontal pager with page indicators
   - Smooth animations and transitions

3. **🔐 Authentication**
   - **Login Screen** with email/password
   - **Register Screen** with password strength indicator
   - Social login buttons (Google, Facebook)
   - Form validation and loading states
   - Animated text fields

4. **🏠 Home Screen**
   - Welcome header with location
   - Search bar with voice search icon
   - Category selection with animated chips
   - Promotional banners carousel
   - Popular restaurants horizontal scroll
   - Recommended foods grid
   - Beautiful gradient cards

### 🎭 Animations & Interactions

- **Page Transitions**: Slide animations between screens
- **Micro-interactions**: Button press effects, favorite heart animation
- **Loading States**: Shimmer effects and progress indicators
- **Smooth Scrolling**: Horizontal and vertical scrolling with momentum
- **Scale Animations**: Category selection and card interactions

### 🎨 Design System

#### Color Palette
```kotlin
val OrangePrimary = Color(0xFFFF6B35)      // Vibrant orange
val OrangeSecondary = Color(0xFFFFA726)    // Light orange
val RedAccent = Color(0xFFFF5722)          // Badges & notifications
val DarkGray = Color(0xFF2C3E50)           // Primary text
val LightGray = Color(0xFFF8F9FA)          // Background
```

#### Spacing & Dimensions
- Consistent spacing system (4dp, 8dp, 16dp, 24dp, 32dp)
- Corner radius variations (8dp, 12dp, 16dp, 20dp)
- Elevation levels for depth and hierarchy

## 🛠️ Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (ready for implementation)
- **Navigation**: Navigation Compose
- **Animations**: Compose Animations
- **Image Loading**: Coil (dependency added)
- **System UI**: Accompanist System UI Controller

## 📦 Dependencies

```kotlin
// Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.12.01"))

// Core Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose")

// Navigation
implementation("androidx.navigation:navigation-compose:2.8.5")

// Animations & UI
implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
implementation("com.google.accompanist:accompanist-pager:0.32.0")
implementation("io.coil-kt:coil-compose:2.5.0")
```

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd WaveOfFood
   ```

2. **Open in Android Studio**
   - Make sure you have Android Studio Arctic Fox or newer
   - Open the project directory

3. **Sync & Build**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio

## 📱 Screen Flow

```
Splash Screen → Onboarding → Login/Register → Home Screen
                    ↓
            (Additional screens coming soon)
            Restaurant Detail → Food Detail → Cart → Profile
```

## 🎯 Coming Soon

- **Restaurant Detail Screen** with menu and reviews
- **Food Detail Screen** with customization options  
- **Cart Screen** with order management
- **Profile Screen** with user settings
- **Search & Filter** functionality
- **Real API Integration**
- **Order Tracking**
- **Payment Integration**

## 🎨 Design Highlights

### Custom Components
- `FoodCard` - Beautiful food item cards with animations
- `CategoryChip` - Animated category selection
- `RatingDisplay` - Star rating component
- `GradientButton` - Gradient action buttons
- `QuantitySelector` - Quantity picker with animations

### Animation Features
- **Spring Animations** for natural feel
- **Staggered Animations** for list items
- **Parallax Effects** in detail screens
- **Gesture Animations** for interactions

## 🔧 Customization

### Colors
Edit `ui/theme/Color.kt` to customize the color palette:
```kotlin
val OrangePrimary = Color(0xFFYourColor)
```

### Typography
Update `ui/theme/Type.kt` for custom fonts:
```kotlin
val PoppinsFontFamily = FontFamily(/* Add your fonts */)
```

### Animations
Modify animation duration in `ui/theme/Dimensions.kt`:
```kotlin
object AnimationDuration {
    const val fast = 150
    const val normal = 300
}
```

## 📱 Screenshots

*Screenshots will be added once the app is fully functional*

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🎉 Acknowledgments

- Material Design 3 guidelines
- Jetpack Compose documentation
- Android developer community
- Food delivery app design inspirations

---

### 💡 Notes for Developer

- The app currently uses placeholder data and emojis for food images
- You can replace emojis with actual images using Coil
- Add your own Google Fonts by downloading Poppins font files
- Implement backend API integration for real data
- Add proper error handling and loading states
- Consider adding Hilt for dependency injection

**Ready to create an amazing food ordering experience! 🚀**
