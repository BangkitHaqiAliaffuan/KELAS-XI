// Script untuk menambahkan admin baru ke Firebase
const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

// Initialize Firebase Admin
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const auth = admin.auth();
const db = admin.firestore();

async function createAdminUser() {
  try {
    console.log('🚀 Creating new admin user...');
    
    // Admin user details
    const adminEmail = 'admingeteng@geteng.com';
    const adminPassword = 'admin123';
    const adminName = 'Admin Geteng';
    
    // Check if user already exists
    try {
      const existingUser = await auth.getUserByEmail(adminEmail);
      console.log('✅ User already exists:', existingUser.uid);
      
      // Update user info
      await auth.updateUser(existingUser.uid, {
        displayName: adminName,
        emailVerified: true
      });
      
      // Add to Firestore admins collection
      await addToFirestore(existingUser.uid, adminEmail, adminName);
      
    } catch (error) {
      if (error.code === 'auth/user-not-found') {
        console.log('📝 User not found, creating new user...');
        
        // Create new user
        const userRecord = await auth.createUser({
          email: adminEmail,
          password: adminPassword,
          displayName: adminName,
          emailVerified: true
        });
        
        console.log('✅ Successfully created user:', userRecord.uid);
        
        // Add to Firestore admins collection
        await addToFirestore(userRecord.uid, adminEmail, adminName);
        
        // Set custom claims for admin
        await auth.setCustomUserClaims(userRecord.uid, { 
          admin: true,
          role: 'super_admin'
        });
        
        console.log('✅ Custom claims set for admin access');
        
      } else {
        throw error;
      }
    }
    
    console.log('🎉 Admin setup completed successfully!');
    console.log(`📧 Email: ${adminEmail}`);
    console.log(`🔑 Password: ${adminPassword}`);
    
  } catch (error) {
    console.error('❌ Error creating admin user:', error);
  }
}

async function addToFirestore(uid, email, name) {
  try {
    const adminData = {
      name: name,
      email: email,
      role: 'super_admin',
      status: 'active',
      permissions: {
        canManageAdmins: true,
        canManageMenu: true,
        canManageOrders: true,
        canManageUsers: true,
        canViewAnalytics: true
      },
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      lastLoginAt: admin.firestore.FieldValue.serverTimestamp(),
      createdBy: 'system'
    };
    
    await db.collection('admins').doc(uid).set(adminData);
    console.log('✅ Admin added to Firestore collection');
    
  } catch (error) {
    console.error('❌ Error adding to Firestore:', error);
  }
}

// Run the script
createAdminUser().then(() => {
  console.log('✅ Script completed');
  process.exit(0);
}).catch((error) => {
  console.error('❌ Script failed:', error);
  process.exit(1);
});
