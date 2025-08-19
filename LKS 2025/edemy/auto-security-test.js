// AUTO SECURITY TEST SCRIPT
// Copy-paste ke browser console untuk test otomatis

console.log('🔒 AUTO SECURITY TEST STARTING...');

const delay = ms => new Promise(resolve => setTimeout(resolve, ms));

const testEducatorAccess = async () => {
  console.log('\n=== TESTING EDUCATOR ACCESS ===');
  
  // 1. Check current role
  const role = window.checkMyRole ? window.checkMyRole() : 'unknown';
  console.log('Current Role:', role);
  
  // 2. Test access to educator dashboard
  console.log('Testing /educator/dashboard access...');
  
  const currentPath = window.location.pathname;
  
  if (currentPath !== '/educator/dashboard') {
    console.log('Navigating to educator dashboard...');
    window.location.href = '/educator/dashboard';
    return; // Exit here as page will reload
  }
  
  // 3. If we're here, check if we should be here
  console.log('Current path:', currentPath);
  
  if (role.includes('educator')) {
    console.log('✅ CORRECT: Educator can access educator dashboard');
  } else {
    console.log('🚨 SECURITY BREACH: Non-educator accessing educator dashboard!');
    console.log('Expected: Redirect to /courses');
    console.log('Actual: Still on educator dashboard');
  }
  
  // 4. Test navigation to other educator pages
  const educatorPages = [
    '/educator/add-course',
    '/educator/my-courses', 
    '/educator/students'
  ];
  
  for (const page of educatorPages) {
    console.log(`Testing access to ${page}...`);
    await delay(1000);
    
    // We'll just log what should happen rather than actually navigate
    if (role.includes('educator')) {
      console.log(`✅ Should allow access to ${page}`);
    } else {
      console.log(`🚨 Should block access to ${page}`);
    }
  }
};

const testStudentAccess = async () => {
  console.log('\n=== TESTING STUDENT ACCESS ===');
  
  const role = window.checkMyRole ? window.checkMyRole() : 'unknown';
  
  // Test student pages
  const studentPages = ['/courses', '/my-courses'];
  
  for (const page of studentPages) {
    console.log(`Testing access to ${page}...`);
    
    if (role.includes('student') || !role.includes('educator')) {
      console.log(`✅ Should allow access to ${page}`);
    } else {
      console.log(`⚠️ Educator accessing student page ${page} (may be allowed)`);
    }
  }
};

// Run tests
const runAllTests = async () => {
  try {
    await testEducatorAccess();
    await delay(2000);
    await testStudentAccess();
    
    console.log('\n=== SECURITY TEST COMPLETE ===');
    console.log('Check console output above for any security issues.');
    
  } catch (error) {
    console.error('Test error:', error);
  }
};

// Auto-run if we have the role checking function
if (window.checkMyRole) {
  runAllTests();
} else {
  console.log('❌ checkMyRole function not found. Run the main test script first.');
}

// Additional manual test functions
window.forceEducatorAccess = () => {
  console.log('🧪 FORCING educator dashboard access...');
  window.location.href = '/educator/dashboard';
};

window.forceStudentAccess = () => {
  console.log('🧪 FORCING student portal access...');
  window.location.href = '/courses';
};

console.log('\n=== MANUAL TEST FUNCTIONS ===');
console.log('• forceEducatorAccess() - Try to access educator dashboard');
console.log('• forceStudentAccess() - Go to student portal');
