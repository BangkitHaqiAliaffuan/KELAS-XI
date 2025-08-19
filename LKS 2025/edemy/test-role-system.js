// Test Script untuk Simple Role-Based Authentication
// CARA PENGGUNAAN:
// 1. Buka aplikasi di browser (npm run dev)
// 2. Login sebagai user
// 3. Buka Developer Tools (F12) -> Console
// 4. Copy-paste script ini ke console dan tekan Enter

// Check if running in browser
if (typeof window === 'undefined') {
  console.error('❌ Script ini harus dijalankan di BROWSER CONSOLE, bukan Node.js!');
  console.log('📋 LANGKAH-LANGKAH:');
  console.log('1. Jalankan: npm run dev');
  console.log('2. Buka browser ke http://localhost:5173');
  console.log('3. Login sebagaai user');
  console.log('4. Tekan F12 -> Console');
  console.log('5. Copy-paste script ini ke console');
  process.exit(1);
}

console.log('=== EDEMY SIMPLE AUTH TESTING ===');

// 1. Check if Clerk is loaded
if (!window.Clerk) {
  console.error('❌ Clerk is not loaded yet!');
  console.log('⏳ Wait a moment and try again...');
  console.log('💡 Or run: setTimeout(() => { /* paste script here */ }, 2000)');
  throw new Error('Clerk not ready');
}

// 2. Check if user is authenticated
if (!window.Clerk.user) {
  console.error('❌ No user logged in!');
  console.log('🔐 Please login first');
  throw new Error('User not authenticated');
}

// 3. Check basic user info
console.log('✅ User authenticated');
console.log('User Email:', window.Clerk?.user?.emailAddresses[0]?.emailAddress);
console.log('User ID:', window.Clerk?.user?.id);

// 4. Debug organization memberships
const memberships = window.Clerk?.user?.organizationMemberships || [];
console.log('\n=== ORGANIZATION MEMBERSHIPS DEBUG ===');
console.log('Total Organization Memberships:', memberships.length);

// Log all memberships for debugging
memberships.forEach((membership, index) => {
  console.log(`\nMembership ${index + 1}:`);
  console.log('  Organization Name:', membership.organization.name);
  console.log('  Organization ID:', membership.organization.id);
  console.log('  User Role:', membership.role);
  console.log('  Permission Level:', membership.permissions);
});

// 5. Check specifically for "edemy" organization (case insensitive)
const edemyMembership = memberships.find(
  membership => membership.organization.name.toLowerCase() === 'edemy'
);

// Helper function untuk role checking (copy dari roleHelpers.js)
const isEducatorRole = (role) => {
  if (!role) return false;
  
  const normalizedRole = role.toLowerCase();
  return normalizedRole === 'educator' || 
         normalizedRole === 'org:educator' || 
         normalizedRole === 'admin' || 
         normalizedRole === 'org:admin';
};

console.log('\n=== ROLE DETECTION ===');

if (!edemyMembership) {
  console.log('❌ NOT member of Edemy organization');
  console.log('➡️ Role: STUDENT');
  console.log('➡️ Access: Student portal only (/courses)');
} else {
  console.log('✅ Member of Edemy organization');
  console.log('Organization Role:', edemyMembership.role);
  
  if (isEducatorRole(edemyMembership.role)) {
    console.log('✅ Role: EDUCATOR');
    console.log('➡️ Access: Educator portal (/educator/dashboard)');
  } else {
    console.log('⚠️ Role: STUDENT (organization member but not educator)');
    console.log('➡️ Access: Student portal only (/courses)');
  }
}

console.log('\n=== EXPECTED BEHAVIOR ===');
if (!edemyMembership) {
  console.log('• Can register/login at homepage');
  console.log('• Redirected to /courses after auth');
  console.log('• Cannot access /educator/dashboard');
  console.log('• /educator redirects to /courses');
} else if (!isEducatorRole(edemyMembership.role)) {
  console.log('• Can login at homepage');
  console.log('• Redirected to /courses after auth');
  console.log('• Cannot access /educator/dashboard');
  console.log('• /educator redirects to /courses');
} else {
  console.log('• Can login at /educator');
  console.log('• Redirected to /educator/dashboard after auth');
  console.log('• Full educator portal access');
  console.log('• Cannot access student-only features');
}

console.log('\n=== QUICK TESTS ===');
console.log('• Test educator access: window.location.href = "/educator"');
console.log('• Test student access: window.location.href = "/courses"');
console.log('• Logout: window.Clerk.signOut()');
console.log('• Check role: checkMyRole()');

// Quick role check function - UPDATED to match useUserRole implementation
window.checkMyRole = () => {
  console.log('\n=== DETAILED ROLE CHECK ===');
  
  // Check via Clerk.user (like test script above)
  const userMemberships = window.Clerk?.user?.organizationMemberships || [];
  console.log('Via Clerk.user.organizationMemberships:', userMemberships.length, 'memberships');
  
  const edemyMembershipDirect = userMemberships.find(
    membership => membership.organization.name.toLowerCase() === 'edemy'
  );
  
  if (edemyMembershipDirect) {
    console.log('✅ Found edemy membership (direct):', edemyMembershipDirect.role);
  } else {
    console.log('❌ No edemy membership found (direct)');
  }
  
  // Check if we can access React hook data (if available)
  if (window.React) {
    console.log('⚠️ Cannot access React hook data from console');
    console.log('💡 The useUserRole hook uses useOrganizationList which might have different timing');
  }
  
  // Return role based on direct check
  if (!edemyMembershipDirect) {
    return 'student (no organization)';
  }
  
  // Use helper function
  if (isEducatorRole(edemyMembershipDirect.role)) {
    return 'educator';
  }
  
  return 'student (organization member)';
};

// Add additional debugging functions
window.debugClerkData = () => {
  console.log('\n=== FULL CLERK DEBUG ===');
  console.log('Clerk user object:', window.Clerk?.user);
  console.log('Organization memberships:', window.Clerk?.user?.organizationMemberships);
  console.log('Organization memberships length:', window.Clerk?.user?.organizationMemberships?.length);
  console.log('Public metadata:', window.Clerk?.user?.publicMetadata);
};

// Enhanced role check function with detailed debugging
window.checkMyRoleDetailed = () => {
  console.log('\n=== DETAILED ROLE ANALYSIS ===');
  
  const userMemberships = window.Clerk?.user?.organizationMemberships || [];
  console.log('Raw memberships array:', userMemberships);
  console.log('Array length:', userMemberships.length);
  console.log('Is array?', Array.isArray(userMemberships));
  console.log('Is empty?', userMemberships.length === 0);
  
  if (userMemberships.length === 0) {
    console.log('🎯 CONCLUSION: No organization memberships = STUDENT only');
    return 'student (no organizations)';
  }
  
  const edemyMembership = userMemberships.find(
    membership => membership.organization.name.toLowerCase() === 'edemy'
  );
  
  if (!edemyMembership) {
    console.log('🎯 CONCLUSION: Has organizations but not edemy = STUDENT');
    return 'student (not edemy member)';
  }
  
  console.log('Found edemy membership:', edemyMembership);
  
  // Use the same logic as roleHelpers
  if (edemyMembership.role === 'educator' || edemyMembership.role === 'org:educator' || 
      edemyMembership.role === 'admin' || edemyMembership.role === 'org:admin') {
    console.log('🎯 CONCLUSION: Has edemy educator role = EDUCATOR');
    return 'educator';
  }
  
  console.log('🎯 CONCLUSION: Has edemy but not educator role = STUDENT');
  return 'student (edemy member but not educator)';
};

window.testEducatorAccess = () => {
  console.log('\n=== TESTING EDUCATOR ACCESS ===');
  const currentPath = window.location.pathname;
  console.log('Current path:', currentPath);
  
  if (currentPath.startsWith('/educator')) {
    console.log('✅ Currently on educator route');
  } else {
    console.log('➡️ Navigating to educator route...');
    window.location.href = '/educator/dashboard';
  }
};

console.log('Current Role:', window.checkMyRole());
console.log('\n=== ADDITIONAL DEBUG FUNCTIONS ===');
console.log('• Full debug: debugClerkData()');
console.log('• Detailed analysis: checkMyRoleDetailed()');
console.log('• Test access: testEducatorAccess()');
console.log('\n=== TESTING COMPLETE ===');
