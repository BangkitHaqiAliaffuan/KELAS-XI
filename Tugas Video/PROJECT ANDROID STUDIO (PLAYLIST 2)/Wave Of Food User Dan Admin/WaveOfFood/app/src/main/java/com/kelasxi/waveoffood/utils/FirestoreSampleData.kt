package com.kelasxi.waveoffood.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.kelasxi.waveoffood.models.FoodItemModel

/**
 * Utility class untuk mengimport data sample ke Firestore
 * Jalankan sekali saja saat pertama kali setup aplikasi
 */
object FirestoreSampleData {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Import data sample menu ke Firestore
     * Panggil method ini dari Activity atau Fragment
     */
    fun importSampleMenuData(context: Context? = null) {
        android.util.Log.d("FirestoreImport", "🚀 MULAI IMPORT DATA - Function dipanggil!")
        
        val menuItems = getSampleMenuData()
        
        android.util.Log.d("FirestoreImport", "📊 Total menu items to import: ${menuItems.size}")
        context?.let { 
            Toast.makeText(it, "Memulai import ${menuItems.size} data menu...", Toast.LENGTH_SHORT).show()
        }
        
        var successCount = 0
        var errorCount = 0
        
        menuItems.forEachIndexed { index, foodItem ->
            android.util.Log.d("FirestoreImport", "📝 Processing item ${index + 1}: ${foodItem.foodName}")
            
            firestore.collection("menu")
                .add(foodItem)
                .addOnSuccessListener { documentReference ->
                    successCount++
                    android.util.Log.d("FirestoreImport", "✅ Document ${index + 1} added with ID: ${documentReference.id} - ${foodItem.foodName}")
                    
                    // Show success message when all items are imported
                    if (successCount + errorCount == menuItems.size) {
                        val message = "Import selesai! Berhasil: $successCount, Gagal: $errorCount"
                        android.util.Log.d("FirestoreImport", "🎉 $message")
                        context?.let {
                            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    errorCount++
                    android.util.Log.w("FirestoreImport", "❌ Error adding document ${index + 1} - ${foodItem.foodName}", e)
                    
                    // Show result message when all items are processed
                    if (successCount + errorCount == menuItems.size) {
                        val message = "Import selesai! Berhasil: $successCount, Gagal: $errorCount"
                        android.util.Log.d("FirestoreImport", "🎉 $message")
                        context?.let {
                            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
        
        android.util.Log.d("FirestoreImport", "✅ Import process initiated for all ${menuItems.size} items")
    }
    
    /**
     * Data sample menu untuk aplikasi WaveOfFood
     */
    private fun getSampleMenuData(): List<FoodItemModel> {
        return listOf(
            // Indonesian Food
            FoodItemModel(
                id = "",
                name = "Nasi Gudeg",
                price = 25000,
                description = "Nasi gudeg khas Yogyakarta dengan ayam kampung, telur, dan sambal krecek yang pedas manis",
                imageUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=500&h=400&fit=crop&crop=center",
                categoryId = "Indonesian Food",
                rating = 4.5,
                isPopular = true,
                preparationTime = 20
            ),
            FoodItemModel(
                id = "",
                name = "Rendang Daging",
                price = 35000,
                description = "Rendang daging sapi autentik Padang dengan bumbu rempah yang kaya dan santan yang gurih",
                imageUrl = "https://images.unsplash.com/photo-1525755662778-989d0524087e?w=500&h=400&fit=crop&crop=center",
                categoryId = "Indonesian Food",
                rating = 4.8,
                isPopular = true,
                preparationTime = 30
            ),
            FoodItemModel(
                id = "",
                name = "Gado-Gado",
                price = 20000,
                description = "Salad Indonesia dengan sayuran rebus, tahu, tempe, dan saus kacang yang lezat",
                imageUrl = "https://images.unsplash.com/photo-1623855244461-8b3c0de25a54?w=500&h=400&fit=crop&crop=center",
                categoryId = "Indonesian Food",
                rating = 4.2,
                isPopular = false,
                preparationTime = 15
            ),
            FoodItemModel(
                id = "",
                name = "Sate Ayam",
                price = 30000,
                description = "Sate ayam bakar dengan bumbu kacang yang gurih dan lontong sebagai pelengkap",
                imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=500&h=400&fit=crop&crop=center",
                categoryId = "Indonesian Food",
                rating = 4.6,
                isPopular = true,
                preparationTime = 25
            ),
            FoodItemModel(
                id = "",
                name = "Bakso Malang",
                price = 18000,
                description = "Bakso daging sapi dengan kuah kaldu yang gurih, mie, dan aneka pelengkap khas Malang",
                imageUrl = "https://images.unsplash.com/photo-1598511726623-d2e9996892f0?w=500&h=400&fit=crop&crop=center",
                categoryId = "Indonesian Food",
                rating = 4.3,
                isPopular = false,
                preparationTime = 15
            ),
            FoodItemModel(
                id = "",
                name = "Ayam Geprek",
                price = 22000,
                description = "Ayam goreng crispy yang digeprek dengan sambal pedas dan nasi putih hangat",
                imageUrl = "https://images.unsplash.com/photo-1606728035253-49e8a23146de?w=500&h=400&fit=crop&crop=center",
                categoryId = "Indonesian Food",
                rating = 4.4,
                isPopular = true,
                preparationTime = 20
            ),
            FoodItemModel(
                id = "",
                name = "Nasi Padang",
                price = 28000,
                description = "Nasi putih dengan berbagai lauk khas Padang seperti rendang, gulai, dan sayur",
                imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&h=400&fit=crop&crop=center",
                categoryId = "Indonesian Food",
                rating = 4.7,
                isPopular = true,
                preparationTime = 15
            ),
            FoodItemModel(
                id = "",
                name = "Mie Ayam",
                price = 15000,
                description = "Mie kuning dengan topping ayam, bakso, dan sayuran dalam kuah kaldu yang gurih",
                imageUrl = "https://images.unsplash.com/photo-1516684669134-de6f7c473a2a?w=500&h=400&fit=crop&crop=center",
                categoryId = "Indonesian Food",
                rating = 4.1,
                isPopular = false,
                preparationTime = 12
            ),
            
            // Western Food
            FoodItemModel(
                id = "",
                name = "Burger Classic",
                price = 45000,
                description = "Burger daging sapi juicy dengan keju, selada, tomat, dan kentang goreng",
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&h=400&fit=crop&crop=center",
                categoryId = "Western Food",
                rating = 4.5,
                isPopular = true,
                preparationTime = 18
            ),
            FoodItemModel(
                id = "",
                name = "Pizza Margherita",
                price = 85000,
                description = "Pizza Italia asli dengan saus tomat, mozzarella segar, dan daun basil",
                imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=500&h=400&fit=crop&crop=center",
                categoryId = "Western Food",
                rating = 4.7,
                isPopular = true,
                preparationTime = 25
            ),
            FoodItemModel(
                id = "",
                name = "Pasta Carbonara",
                price = 55000,
                description = "Pasta dengan saus krim putih, bacon crispy, dan keju parmesan",
                imageUrl = "https://images.unsplash.com/photo-1621996346565-e3dbc927d86d?w=500&h=400&fit=crop&crop=center",
                categoryId = "Western Food",
                rating = 4.4,
                isPopular = false,
                preparationTime = 20
            ),
            FoodItemModel(
                id = "",
                name = "Steak Tenderloin",
                price = 120000,
                description = "Daging sapi tenderloin premium dengan kentang tumbuk dan sayuran rebus",
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500&h=400&fit=crop&crop=center",
                categoryId = "Western Food",
                rating = 4.9,
                isPopular = true,
                preparationTime = 35
            ),
            
            // Asian Food
            FoodItemModel(
                id = "",
                name = "Ramen Spicy",
                price = 38000,
                description = "Ramen Jepang dengan kuah pedas, telur rebus, dan irisan daging babi",
                imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=500&h=400&fit=crop&crop=center",
                categoryId = "Asian Food",
                rating = 4.6,
                isPopular = true,
                preparationTime = 20
            ),
            FoodItemModel(
                id = "",
                name = "Dimsum Special",
                price = 42000,
                description = "Berbagai macam dimsum isi udang, ayam, dan sayuran dengan saus pedas manis",
                imageUrl = "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=500&h=400&fit=crop&crop=center",
                categoryId = "Asian Food",
                rating = 4.3,
                isPopular = false,
                preparationTime = 25
            ),
            FoodItemModel(
                id = "",
                name = "Pad Thai",
                price = 35000,
                description = "Mie Thailand yang digoreng dengan udang, telur, tauge, dan saus tamarind",
                imageUrl = "https://images.unsplash.com/photo-1559314809-0f31657def5e?w=500&h=400&fit=crop&crop=center",
                categoryId = "Asian Food",
                rating = 4.4,
                isPopular = true,
                preparationTime = 18
            ),
            
            // Drinks
            FoodItemModel(
                id = "",
                name = "Es Teh Manis",
                price = 5000,
                description = "Teh manis dingin yang menyegarkan, cocok untuk cuaca panas",
                imageUrl = "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500&h=400&fit=crop&crop=center",
                categoryId = "Drinks",
                rating = 4.0,
                isPopular = false,
                preparationTime = 5
            ),
            FoodItemModel(
                id = "",
                name = "Jus Alpukat",
                price = 12000,
                description = "Jus alpukat segar dengan susu kental manis dan es batu",
                imageUrl = "https://images.unsplash.com/photo-1623065422902-4076cb6d4bd0?w=500&h=400&fit=crop&crop=center",
                categoryId = "Drinks",
                rating = 4.2,
                isPopular = false,
                preparationTime = 8
            ),
            FoodItemModel(
                id = "",
                name = "Coffee Latte",
                price = 25000,
                description = "Kopi latte dengan art yang cantik, dibuat dari biji kopi pilihan premium",
                imageUrl = "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=500&h=400&fit=crop&crop=center",
                categoryId = "Drinks",
                rating = 4.5,
                isPopular = true,
                preparationTime = 10
            ),
            FoodItemModel(
                id = "",
                name = "Smoothie Bowl",
                price = 32000,
                description = "Smoothie mangga dengan topping granola, buah segar, dan madu",
                imageUrl = "https://images.unsplash.com/photo-1511690743698-d9d85f2fbf38?w=500&h=400&fit=crop&crop=center",
                categoryId = "Drinks",
                rating = 4.3,
                isPopular = false,
                preparationTime = 12
            ),
            
            // Desserts
            FoodItemModel(
                id = "",
                name = "Chocolate Lava Cake",
                price = 28000,
                description = "Kue coklat hangat dengan inti coklat cair yang meleleh, disajikan dengan es krim vanilla",
                imageUrl = "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&h=400&fit=crop&crop=center",
                categoryId = "Desserts",
                rating = 4.8,
                isPopular = true,
                preparationTime = 15
            ),
            FoodItemModel(
                id = "",
                name = "Tiramisu Classic",
                price = 35000,
                description = "Dessert Italia dengan lapisan mascarpone, ladyfinger, dan kopi yang lembut",
                imageUrl = "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=500&h=400&fit=crop&crop=center",
                categoryId = "Desserts",
                rating = 4.6,
                isPopular = true,
                preparationTime = 10
            ),
            FoodItemModel(
                id = "",
                name = "Ice Cream Sundae",
                price = 22000,
                description = "Es krim vanilla dengan topping coklat, kacang, cherry, dan whipped cream",
                imageUrl = "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=500&h=400&fit=crop&crop=center",
                categoryId = "Desserts",
                rating = 4.2,
                isPopular = false,
                preparationTime = 8
            )
        )
    }
}
