const admin = require('firebase-admin');

// Initialize Firebase Admin SDK
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

async function refreshAdminClaims() {
  try {
    console.log('Refreshing admin claims...');
    
    const adminEmails = [
      'admin@waveoffood.com',
      'admin@kelasxi.com'
    ];
    
    for (const email of adminEmails) {
      try {
        // Get user by email
        const user = await admin.auth().getUserByEmail(email);
        
        // Set custom claims again to refresh
        await admin.auth().setCustomUserClaims(user.uid, { 
          admin: true,
          role: 'admin',
          timestamp: Date.now()
        });
        
        console.log(`✅ Refreshed admin claims for: ${email}`);
        
        // Verify the claim was set
        const userRecord = await admin.auth().getUser(user.uid);
        console.log(`Claims for ${email}:`, userRecord.customClaims);
        
      } catch (error) {
        console.error(`❌ Error refreshing claims for ${email}:`, error.message);
      }
    }
    
    console.log('\n🔄 Admin claims refreshed successfully!');
    console.log('Admin users need to logout and login again to get fresh tokens.');
    
    process.exit(0);
    
  } catch (error) {
    console.error('❌ Error refreshing admin claims:', error);
    process.exit(1);
  }
}

refreshAdminClaims();
