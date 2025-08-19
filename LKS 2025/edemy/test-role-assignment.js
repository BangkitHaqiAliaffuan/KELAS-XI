// Test Role Assignment
// Buka browser console dan jalankan ini setelah login

// Check current user role
console.log('Current user role:', window.Clerk?.user?.publicMetadata?.role);

// Manually assign role (untuk testing)
// window.Clerk?.user?.update({
//   publicMetadata: { role: 'educator' }
// }).then(() => {
//   console.log('Role updated to educator');
//   window.location.reload();
// });

// Check if role assignment hook works
console.log('Role assignment system loaded');

// Test forceRedirectUrl
console.log('Student auth should redirect to /courses');
console.log('Educator auth should redirect to /educator/dashboard');
