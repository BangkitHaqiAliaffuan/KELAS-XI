/**
 * Firebase Enhanced Data Import Script
 * WaveOfFood - Professional Design Integration
 * 
 * CARA PENGGUNAAN:
 * 1. npm install firebase-admin
 * 2. Download service account key dari Firebase Console
 * 3. Rename service account key menjadi 'serviceAccountKey.json' 
 * 4. node firebase-import-enhanced.js
 */

const admin = require('firebase-admin');
const fs = require('fs');

// Initialize Firebase Admin
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

async function importEnhancedData() {
  try {
    console.log('🔥 Starting Firebase Enhanced Data Import...');
    
    // Read enhanced data
    const enhancedData = JSON.parse(fs.readFileSync('./enhanced-data.json', 'utf8'));
    
    // Import Categories
    console.log('📁 Importing Categories...');
    const categoriesRef = db.collection('categories');
    for (const [id, category] of Object.entries(enhancedData.categories)) {
      await categoriesRef.doc(id).set({
        ...category,
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      });
      console.log(`✅ Category imported: ${category.name}`);
    }
    
    // Import Foods
    console.log('🍽️ Importing Foods...');
    const foodsRef = db.collection('foods');
    for (const [id, food] of Object.entries(enhancedData.foods)) {
      await foodsRef.doc(id).set({
        ...food,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: admin.firestore.FieldValue.serverTimestamp()
      });
      console.log(`✅ Food imported: ${food.name}`);
    }
    
    // Import Promotions
    console.log('🎉 Importing Promotions...');
    const promotionsRef = db.collection('promotions');
    for (const [id, promotion] of Object.entries(enhancedData.promotions)) {
      await promotionsRef.doc(id).set({
        ...promotion,
        validFrom: new Date(promotion.validFrom),
        validUntil: new Date(promotion.validUntil),
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      });
      console.log(`✅ Promotion imported: ${promotion.title}`);
    }
    
    console.log('🎯 Enhanced Data Import Complete!');
    console.log('📊 Summary:');
    console.log(`- Categories: ${Object.keys(enhancedData.categories).length}`);
    console.log(`- Foods: ${Object.keys(enhancedData.foods).length}`);
    console.log(`- Promotions: ${Object.keys(enhancedData.promotions).length}`);
    
    process.exit(0);
    
  } catch (error) {
    console.error('❌ Import Error:', error);
    process.exit(1);
  }
}

importEnhancedData();
