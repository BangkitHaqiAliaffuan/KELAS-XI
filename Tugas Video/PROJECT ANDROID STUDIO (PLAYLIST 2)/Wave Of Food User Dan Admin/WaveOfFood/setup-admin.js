const admin = require('firebase-admin');

// Initialize Firebase Admin SDK
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

async function setAdminClaim(email) {
  try {
    // Get user by email
    const user = await admin.auth().getUserByEmail(email);
    
    // Set custom claims
    await admin.auth().setCustomUserClaims(user.uid, { admin: true });
    
    console.log(`Successfully set admin claim for user: ${email}`);
    console.log(`User UID: ${user.uid}`);
    
    // Verify the claim was set
    const userRecord = await admin.auth().getUser(user.uid);
    console.log('Custom claims:', userRecord.customClaims);
    
  } catch (error) {
    console.error('Error setting admin claim:', error);
    
    // If user doesn't exist, create it
    if (error.code === 'auth/user-not-found') {
      console.log('User not found, creating admin user...');
      try {
        const newUser = await admin.auth().createUser({
          email: email,
          password: 'admin123', // Default password
          emailVerified: true
        });
        
        // Set admin claim for new user
        await admin.auth().setCustomUserClaims(newUser.uid, { admin: true });
        console.log(`Created admin user: ${email}`);
        console.log(`User UID: ${newUser.uid}`);
        console.log('Default password: admin123');
        
      } catch (createError) {
        console.error('Error creating admin user:', createError);
      }
    }
  }
}

// Set admin claims for admin emails
const adminEmails = [
  'admin@waveoffood.com',
  'admin@kelasxi.com'
];

async function setupAdmins() {
  for (const email of adminEmails) {
    await setAdminClaim(email);
  }
  
  console.log('\nAdmin setup complete!');
  console.log('You can now login with any of these admin accounts:');
  adminEmails.forEach(email => {
    console.log(`- ${email} (password: admin123)`);
  });
  
  process.exit(0);
}

setupAdmins();
