// Script untuk menambahkan admin ke Firestore saja
const { initializeApp } = require('firebase/app');
const { getFirestore, doc, setDoc, serverTimestamp } = require('firebase/firestore');

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyDGFhvJ7-QIHaJKNYi_hMHO4f3jT4B6Zrs",
  authDomain: "waveoffood-889a6.firebaseapp.com",
  projectId: "waveoffood-889a6",
  storageBucket: "waveoffood-889a6.firebasestorage.app",
  messagingSenderId: "112360782235617310661",
  appId: "1:112360782235617310661:web:4b0f1e9c5d2b8f7a9b3e2c",
  measurementId: "G-ABCDEFGHIJ"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

async function addAdminToFirestore() {
  try {
    console.log('🚀 Adding admin to Firestore...');
    
    // Admin user details - ganti dengan ID yang sesuai
    const adminId = 'admin-geteng-001'; // Temporary ID, nanti akan diganti
    const adminEmail = 'admingeteng@geteng.com';
    const adminName = 'Admin Geteng';
    
    const adminData = {
      name: adminName,
      email: adminEmail,
      role: 'super_admin',
      status: 'active',
      permissions: {
        canManageAdmins: true,
        canManageMenu: true,
        canManageOrders: true,
        canManageUsers: true,
        canViewAnalytics: true
      },
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
      lastLoginAt: null,
      createdBy: 'system'
    };
    
    await setDoc(doc(db, 'admins', adminId), adminData);
    console.log('✅ Admin added to Firestore collection');
    console.log(`📧 Email: ${adminEmail}`);
    console.log(`👤 Name: ${adminName}`);
    console.log(`🔑 Role: super_admin`);
    console.log(`📋 Admin ID: ${adminId}`);
    
    console.log('🎉 Admin setup completed successfully!');
    console.log('⚠️  Note: You need to create this user in Firebase Authentication manually or use the web interface');
    
  } catch (error) {
    console.error('❌ Error adding admin to Firestore:', error);
  }
}

// Run the script
addAdminToFirestore().then(() => {
  console.log('✅ Script completed');
  process.exit(0);
}).catch((error) => {
  console.error('❌ Script failed:', error);
  process.exit(1);
});
