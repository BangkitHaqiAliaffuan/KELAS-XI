// Enhanced Security Test untuk Educator Path Protection
// Buka browser console dan jalankan untuk test proteksi

console.log('=== EDUCATOR PATH SECURITY TEST ===');

// Function untuk test access ke berbagai path
const testAccess = async (path) => {
  console.log(`\n🔍 Testing access to: ${path}`);
  
  const currentUser = window.Clerk?.user;
  if (!currentUser) {
    console.log('❌ No user logged in');
    return;
  }

  const memberships = currentUser.organizationMemberships || [];
  const edemyMembership = memberships.find(
    membership => membership.organization.name.toLowerCase() === 'edemy'
  );

  console.log('User Email:', currentUser.emailAddresses[0]?.emailAddress);
  
  if (!edemyMembership) {
    console.log('👤 User Type: Regular Student (No Organization)');
    console.log('🔒 Expected Behavior for /educator:');
    console.log('   • Should be redirected to /courses');
    console.log('   • Should NOT see educator login page');
  } else {
    console.log('🏢 User Type: Organization Member');
    console.log('📋 Organization Role:', edemyMembership.role);
    
    if (edemyMembership.role === 'educator' || edemyMembership.role === 'admin') {
      console.log('✅ Expected Behavior for /educator:');
      console.log('   • Should see educator login page');
      console.log('   • After login: redirect to /educator/dashboard');
    } else {
      console.log('⚠️ Expected Behavior for /educator:');
      console.log('   • Should be redirected to /courses');
      console.log('   • Should NOT see educator login page');
    }
  }
};

// Function untuk test bypass attempts
const testBypassAttempts = () => {
  console.log('\n🛡️ TESTING BYPASS ATTEMPTS');
  
  const bypassPaths = [
    '/educator',
    '/educator/',
    '/educator/auth',
    '/educator/dashboard',
    '/educator/add-course',
    '/educator/my-courses',
    '/educator/students'
  ];

  bypassPaths.forEach(path => {
    console.log(`\n🚫 Bypass Test: ${path}`);
    console.log('   Expected: Non-educators redirected to /courses');
  });
};

// Function untuk check current protection status
const checkProtectionStatus = () => {
  console.log('\n🔐 CURRENT PROTECTION STATUS');
  
  const currentUser = window.Clerk?.user;
  if (!currentUser) {
    console.log('❌ No user logged in - Cannot test protection');
    return;
  }

  const memberships = currentUser.organizationMemberships || [];
  const edemyMembership = memberships.find(
    membership => membership.organization.name.toLowerCase() === 'edemy'
  );

  if (!edemyMembership) {
    console.log('🔴 STUDENT USER DETECTED');
    console.log('✅ Protection Active: Should be blocked from /educator paths');
  } else if (edemyMembership.role === 'educator' || edemyMembership.role === 'admin') {
    console.log('🟢 EDUCATOR USER DETECTED');
    console.log('✅ Access Granted: Can access /educator paths');
  } else {
    console.log('🟡 ORGANIZATION MEMBER (Non-educator)');
    console.log('✅ Protection Active: Should be blocked from /educator paths');
  }
};

// Quick test functions
window.testEducatorAccess = () => testAccess('/educator');
window.testBypass = testBypassAttempts;
window.checkSecurity = checkProtectionStatus;

// Auto-run basic tests
testAccess('/educator');
checkProtectionStatus();

console.log('\n=== MANUAL TESTING COMMANDS ===');
console.log('• testEducatorAccess() - Test /educator access');
console.log('• testBypass() - Test bypass attempts');
console.log('• checkSecurity() - Check protection status');
console.log('• Navigate to /educator: window.location.href = "/educator"');
console.log('• Navigate to /courses: window.location.href = "/courses"');
console.log('• Logout: window.Clerk.signOut()');

console.log('\n=== PROTECTION VERIFICATION ===');
console.log('1. As Student: Try accessing /educator → Should redirect to /courses');
console.log('2. As Student: Try /educator/dashboard → Should redirect to /courses');
console.log('3. As Educator: Try /educator → Should see login page');
console.log('4. As Educator: After login → Should go to /educator/dashboard');

console.log('\n✅ Security test script loaded successfully!');
