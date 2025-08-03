// firestore-import.js
// Script untuk mengimport data sample ke Firestore

const admin = require('firebase-admin');
const fs = require('fs');

// Download service account key dari Firebase Console
// Project Settings > Service accounts > Generate new private key
const serviceAccount = require('./waveoffood-service-account.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

async function importData() {
  try {
    console.log('🚀 Starting Firestore data import...');
    
    // Read data from JSON file
    const data = JSON.parse(fs.readFileSync('firestore-data.json', 'utf8'));
    
    // Import menu collection
    const menuData = data.menu;
    const batch = db.batch();
    
    let count = 0;
    for (const [docId, docData] of Object.entries(menuData)) {
      const docRef = db.collection('menu').doc(docId);
      batch.set(docRef, {
        ...docData,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: admin.firestore.FieldValue.serverTimestamp()
      });
      count++;
    }
    
    await batch.commit();
    console.log(`✅ Successfully imported ${count} menu items to Firestore!`);
    
    // Verify data
    const snapshot = await db.collection('menu').get();
    console.log(`📊 Total documents in 'menu' collection: ${snapshot.size}`);
    
    process.exit(0);
  } catch (error) {
    console.error('❌ Error importing data:', error);
    process.exit(1);
  }
}

importData();
