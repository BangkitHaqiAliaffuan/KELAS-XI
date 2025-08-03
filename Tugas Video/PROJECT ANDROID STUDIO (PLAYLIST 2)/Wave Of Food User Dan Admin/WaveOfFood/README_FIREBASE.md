# 🔥 WaveOfFood Firebase Integration

Firebase setup lengkap untuk design enhanced dengan **professional green theme** dan **comprehensive data structure**.

## 📁 Files Created

| File | Purpose |
|------|---------|
| `FIREBASE_SETUP.md` | 📖 Complete setup guide with external steps |
| `enhanced-data.json` | 📊 Sample data (categories, foods, promotions) |
| `firebase-import-enhanced.js` | 🚀 Auto import script |
| `FirebaseRepository.kt` | 💾 Repository class for all Firebase operations |
| `Models.kt` | 🏗️ Enhanced models for new data structure |
| `setup-firebase.bat` | ⚡ Windows setup script |

## 🚀 Quick Setup (Windows)

1. **Download Service Account Key:**
   - Firebase Console → Project Settings → Service Accounts
   - Generate new private key → Download JSON
   - Rename to `serviceAccountKey.json`

2. **Run Auto Setup:**
   ```batch
   setup-firebase.bat
   ```

3. **Manual Setup (if script fails):**
   ```bash
   npm install
   node firebase-import-enhanced.js
   ```

## 📊 Data Structure

### Collections Created:
- **`categories`** - Food categories with images
- **`foods`** - Enhanced food items with nutrition info
- **`promotions`** - Discount promotions with validity
- **`users/{uid}/cart`** - User cart items
- **`users/{uid}/favorites`** - User favorites
- **`orders`** - Complete order history

### Sample Data:
- ✅ **5 Categories:** Pizza, Burger, Indonesian Food, Dessert, Drinks
- ✅ **8 Foods:** Nasi Gudeg, Rendang, Pizza, Burger, etc.
- ✅ **3 Promotions:** Welcome 20%, Weekend 15%, Pizza Monday 25%

## 🔧 Firebase Configuration

### Security Rules Applied:
```javascript
// Categories & Foods - Public read, admin write only
// Users - Owner access only with subcollections
// Orders - Owner access only
// Promotions - Public read, admin write only
```

### Features Integrated:
- ✅ **Authentication:** Email/Password login
- ✅ **Real-time data:** Categories, foods, cart sync
- ✅ **Professional UI:** Green theme, Material 3
- ✅ **Enhanced models:** Nutrition info, promotions, reviews
- ✅ **Cart management:** Add, update, remove, clear
- ✅ **Order system:** Complete checkout flow
- ✅ **User profiles:** Favorites, addresses, loyalty points

## 🏗️ Architecture

```
FirebaseRepository.kt
├── Authentication (register, login, logout)
├── Categories (getCategories)
├── Foods (getPopular, getByCategory, search)
├── Cart (add, get, update, remove, clear)
├── Favorites (add, remove, get)
├── Orders (create, getUserOrders)
├── Promotions (getActive)
└── Profile (update, get)
```

## 🎯 Usage Examples

### Load Categories:
```kotlin
val repository = FirebaseRepository()
repository.getCategories().onSuccess { categories ->
    // Update UI with categories
}
```

### Add to Cart:
```kotlin
val cartItem = CartItemModel(
    id = food.id,
    name = food.name,
    price = food.price,
    quantity = 1
)
repository.addToCart(userId, cartItem)
```

### Create Order:
```kotlin
val order = OrderModel(
    userId = currentUser.uid,
    items = cartItems,
    totalAmount = calculateTotal()
)
repository.createOrder(order)
```

## ⚡ Enhanced Features

- **🎨 Professional Design:** Green theme (#4CAF50) with Material 3
- **📱 Responsive UI:** ConstraintLayout, RecyclerView, CardView
- **🔄 Real-time Sync:** Live updates from Firestore
- **💾 Offline Support:** Firebase offline persistence
- **🔐 Secure:** Proper security rules and user isolation
- **🚀 Performance:** Optimized queries and pagination ready
- **📊 Analytics Ready:** Firebase Analytics integration points

## 🚨 Troubleshooting

### Common Issues:
1. **"FirebaseApp failed to initialize"**
   - Check `google-services.json` in `app/` folder
   - Verify package name: `com.kelasxi.waveoffood`

2. **"Permission denied"**
   - Copy security rules to Firebase Console
   - Ensure user is authenticated

3. **Build errors**
   - Sync Gradle files
   - Clean & rebuild project

### Debug Commands:
```bash
# Check Firebase connection
adb logcat -s Firebase

# Check app logs
adb logcat -s WaveOfFood
```

## 📞 Support

For issues:
1. Check `FIREBASE_SETUP.md` for detailed steps
2. Review Firebase Console error logs
3. Check Android Studio Logcat
4. Verify all external steps completed

**🎉 Happy Coding!**
