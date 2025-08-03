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
            FoodItemModel(
                id = "",
                name = "Nasi Gudeg",
                price = 25000,
                description = "Nasi gudeg khas Yogyakarta dengan ayam kampung, telur, dan sambal krecek yang pedas manis",
                imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400",
                categoryId = "Indonesian Food"
            ),
            FoodItemModel(
                id = "",
                name = "Rendang Daging",
                price = 35000,
                description = "Rendang daging sapi autentik Padang dengan bumbu rempah yang kaya dan santan yang gurih",
                imageUrl = "https://images.unsplash.com/photo-1604521037225-0448dc7c9e31?w=400",
                categoryId = "Indonesian Food"
            ),
            FoodItemModel(
                id = "",
                name = "Gado-Gado",
                price = 20000,
                description = "Salad Indonesia dengan sayuran rebus, tahu, tempe, dan saus kacang yang lezat",
                imageUrl = "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400",
                categoryId = "Indonesian Food"
            ),
            FoodItemModel(
                id = "",
                name = "Sate Ayam",
                price = 30000,
                description = "Sate ayam bakar dengan bumbu kacang yang gurih dan lontong sebagai pelengkap",
                imageUrl = "https://images.unsplash.com/photo-1529042410759-befb1204b468?w=400",
                categoryId = "Indonesian Food"
            ),
            FoodItemModel(
                id = "",
                name = "Bakso Malang",
                price = 18000,
                description = "Bakso daging sapi dengan kuah kaldu yang gurih, mie, dan aneka pelengkap khas Malang",
                imageUrl = "https://images.unsplash.com/photo-1575669090474-5d35b4b96ba1?w=400",
                categoryId = "Indonesian Food"
            ),
            FoodItemModel(
                id = "",
                name = "Ayam Geprek",
                price = 22000,
                description = "Ayam goreng crispy yang digeprek dengan sambal pedas dan nasi putih hangat",
                imageUrl = "https://images.unsplash.com/photo-1562967916-eb82221dfb38?w=400",
                categoryId = "Indonesian Food"
            ),
            FoodItemModel(
                id = "",
                name = "Nasi Padang",
                price = 28000,
                description = "Nasi putih dengan berbagai lauk khas Padang seperti rendang, gulai, dan sayur",
                imageUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400",
                categoryId = "Indonesian Food"
            ),
            FoodItemModel(
                id = "",
                name = "Mie Ayam",
                price = 15000,
                description = "Mie kuning dengan topping ayam, bakso, dan sayuran dalam kuah kaldu yang gurih",
                imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400",
                categoryId = "Indonesian Food"
            ),
            FoodItemModel(
                id = "",
                name = "Es Teh Manis",
                price = 5000,
                description = "Teh manis dingin yang menyegarkan, cocok untuk cuaca panas",
                imageUrl = "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400",
                categoryId = "Drinks"
            ),
            FoodItemModel(
                id = "",
                name = "Jus Alpukat",
                price = 12000,
                description = "Jus alpukat segar dengan susu kental manis dan es batu",
                imageUrl = "https://images.unsplash.com/photo-1623065422902-4076cb6d4bd0?w=400",
                categoryId = "Drinks"
            )
        )
    }
}
