# Quick Fix - Icon Resources

## Error
```
Unresolved reference 'ic_back'
Unresolved reference 'ic_user'
```

## Perbaikan
Mengganti icon references dengan icon yang sudah ada di project:

### 1. Back Button Icon
**Before:**
```kotlin
setNavigationIcon(R.drawable.ic_back)
```

**After:**
```kotlin
setNavigationIcon(R.drawable.ic_arrow_back)  // ✅ Icon sudah ada di project
```

### 2. User Avatar Icon
**Before:**
```kotlin
.placeholder(R.drawable.ic_user)
ivSellerAvatar.setImageResource(R.drawable.ic_user)
```

**After:**
```kotlin
.placeholder(R.drawable.ic_person)  // ✅ Icon sudah ada di project
ivSellerAvatar.setImageResource(R.drawable.ic_person)
```

## Available Icons in Project
- ✅ `ic_arrow_back.xml` - untuk back navigation
- ✅ `ic_person.xml` - untuk user/person avatar
- ✅ `ic_add.xml` - untuk add button
- ✅ `ic_location.xml` - untuk location
- ✅ `ic_home.xml` - untuk home
- Dan banyak lagi...

## Status
✅ **Error resolved** - Project seharusnya bisa di-build sekarang
