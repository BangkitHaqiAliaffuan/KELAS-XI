const admin = require('firebase-admin');
const fs = require('fs');

// Initialize Firebase Admin SDK
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

async function importData() {
  try {
    console.log('Starting data import...');
    
    // Read the enhanced data file
    const data = JSON.parse(fs.readFileSync('./enhanced-data.json', 'utf8'));
    
    // Import categories
    if (data.categories) {
      console.log('Importing categories...');
      const categoriesRef = db.collection('categories');
      for (const [id, categoryData] of Object.entries(data.categories)) {
        await categoriesRef.doc(id).set(categoryData);
        console.log(`Category ${id} imported`);
      }
    }
    
    // Import foods
    if (data.foods) {
      console.log('Importing foods...');
      const foodsRef = db.collection('foods');
      for (const [id, foodData] of Object.entries(data.foods)) {
        await foodsRef.doc(id).set(foodData);
        console.log(`Food ${id} imported`);
      }
    }
    
    // Import users
    if (data.users) {
      console.log('Importing users...');
      const usersRef = db.collection('users');
      for (const [id, userData] of Object.entries(data.users)) {
        await usersRef.doc(id).set(userData);
        console.log(`User ${id} imported`);
      }
    }
    
    // Import orders
    if (data.orders) {
      console.log('Importing orders...');
      const ordersRef = db.collection('orders');
      for (const [id, orderData] of Object.entries(data.orders)) {
        await ordersRef.doc(id).set(orderData);
        console.log(`Order ${id} imported`);
      }
    }
    
    // Import promotions if exists
    if (data.promotions) {
      console.log('Importing promotions...');
      const promotionsRef = db.collection('promotions');
      for (const [id, promoData] of Object.entries(data.promotions)) {
        await promotionsRef.doc(id).set(promoData);
        console.log(`Promotion ${id} imported`);
      }
    }
    
    console.log('✅ Data import completed successfully!');
    console.log('Collections imported:');
    
    // Count documents in each collection
    const collections = ['categories', 'foods', 'users', 'orders'];
    for (const collectionName of collections) {
      const snapshot = await db.collection(collectionName).get();
      console.log(`- ${collectionName}: ${snapshot.size} documents`);
    }
    
    process.exit(0);
    
  } catch (error) {
    console.error('❌ Error importing data:', error);
    process.exit(1);
  }
}

importData();
